package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.request.PosOrderRequest;
import com.jugueteria.api.dto.request.ReturnRequest;
import com.jugueteria.api.dto.response.OrderResponse;
import com.jugueteria.api.entity.*;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.*;
import com.jugueteria.api.services.EmailService;
import com.jugueteria.api.services.OrderService;
import com.jugueteria.api.services.PdfService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;
    private final EmailService emailService;
    private final PdfService pdfService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Pedido createPendingOrder(Usuario usuario) {
        logger.info("Iniciando creación de pedido pendiente para el usuario: {}", usuario.getEmail());
        List<CarritoItem> cartItems = carritoItemRepository.findByUsuario(usuario);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("El carrito está vacío, no se puede crear un pedido.");
        }
        
        Pedido pedido = new Pedido();
        pedido.setCliente(usuario);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(calculateTotal(cartItems));
        pedido.setDireccionEnvio("Dirección pendiente de confirmación");
        pedido.setTipoVenta("ONLINE");
        
        List<DetallePedido> detalles = cartItems.stream().map(cartItem -> {
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(cartItem.getProducto());
            detalle.setCantidad(cartItem.getCantidad());
            detalle.setPrecioUnitario(cartItem.getProducto().getPrecio());
            return detalle;
        }).collect(Collectors.toList());
        pedido.setDetalles(detalles);
        
        Pedido savedPedido = pedidoRepository.save(pedido);
        logger.info("Pedido pendiente #{} creado exitosamente.", savedPedido.getId());
        return savedPedido;
    }

    @Override
    @Transactional
    public void updateOrderStatusFromWebhook(Long orderId, String status) {
        logger.info("Procesando webhook para Pedido #{} con nuevo estado de MP: {}", orderId, status);
        Pedido pedido = pedidoRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Webhook: Pedido no encontrado con id: " + orderId));

        if (!"PENDIENTE".equalsIgnoreCase(pedido.getEstado())) {
            logger.warn("Webhook: Pedido #{} ya fue procesado o está en otro estado. Estado actual: {}. Abortando.", orderId, pedido.getEstado());
            return;
        }

        if ("approved".equalsIgnoreCase(status)) {
            logger.info("Pago APROBADO para Pedido #{}. Actualizando a 'PROCESANDO'.", orderId);
            pedido.setEstado("PROCESANDO"); // <-- Cambiado a PROCESANDO (según la discusión anterior)
            
            List<CarritoItem> cartItems = carritoItemRepository.findByUsuario(pedido.getCliente());
            
            if (cartItems.isEmpty()) {
                logger.warn("ADVERTENCIA: El carrito del usuario {} ya estaba vacío al procesar el pedido #{}.", pedido.getCliente().getEmail(), orderId);
            } else {
                updateStock(cartItems); 
                carritoItemRepository.deleteAll(cartItems); 
                logger.info("Stock descontado y carrito limpiado para el usuario {}.", pedido.getCliente().getEmail());
            }
            
            emailService.sendOrderConfirmationEmail(pedido);
            
        } else {
            logger.warn("Pago FALLIDO para Pedido #{}. Actualizando a 'PAGO_FALLIDO'.", orderId);
            pedido.setEstado("PAGO_FALLIDO");
        }
        
        pedidoRepository.save(pedido);
        logger.info("Pedido #{} guardado en la base de datos con el nuevo estado: {}", orderId, pedido.getEstado());
    }
    
    @Override
    @Transactional
    public OrderResponse createPhysicalSale(PosOrderRequest request, Usuario worker) {
        Pedido pedido = new Pedido();
        pedido.setCliente(worker); // La venta se registra a nombre del trabajador
        pedido.setEstado("ENTREGADO");
        pedido.setTotal(request.getTotal());
        pedido.setDireccionEnvio("Venta en Tienda");
        pedido.setTipoVenta("FISICA");
        
        List<DetallePedido> detalles = request.getItems().stream().map(itemDto -> {
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemDto.getProductoId()));

            // Descontar stock
            int newStock = producto.getStock() - itemDto.getQuantity();
            if (newStock < 0) {
                throw new IllegalStateException("Stock insuficiente para: " + producto.getNombre());
            }
            producto.setStock(newStock);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.getQuantity());
            detalle.setPrecioUnitario(producto.getPrecio());
            return detalle;
        }).collect(Collectors.toList());
        
        pedido.setDetalles(detalles);
        // Guardar primero el pedido para obtener su ID
        Pedido savedPedido = pedidoRepository.save(pedido);

        // Crear un registro de pago simple para la venta física
        Pago pago = Pago.builder()
                .pedido(savedPedido)
                .estado("approved")
                .metodoPago("fisico") // ej. efectivo, tarjeta en tienda
                .monto(request.getTotal())
                .build();
        pagoRepository.save(pago);
        
        return modelMapper.map(savedPedido, OrderResponse.class);
    }

   @Override
    @Transactional
    public OrderResponse processReturn(ReturnRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + request.getPedidoId()));

        // Solo se pueden procesar devoluciones de pedidos entregados o en estados similares
        if (!pedido.getEstado().equals("ENTREGADO")) {
             throw new IllegalStateException("Solo se pueden procesar devoluciones de pedidos ya entregados.");
        }

        for (ReturnRequest.ReturnItemRequest itemDto : request.getItems()) {
            DetallePedido detalle = pedido.getDetalles().stream()
                .filter(d -> d.getId().equals(itemDto.getDetallePedidoId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El item con ID " + itemDto.getDetallePedidoId() + " no pertenece a este pedido."));

            int cantidadPreviamenteDevuelta = detalle.getCantidadDevuelta() != null ? detalle.getCantidadDevuelta() : 0;
            if (itemDto.getCantidadADevolver() > (detalle.getCantidad() - cantidadPreviamenteDevuelta)) {
                throw new IllegalArgumentException("La cantidad a devolver excede la cantidad comprada y no devuelta para: " + detalle.getProducto().getNombre());
            }

            detalle.setCantidadDevuelta(cantidadPreviamenteDevuelta + itemDto.getCantidadADevolver());
            detalle.setMotivoDevolucion(itemDto.getMotivo());

            if (request.isDevolverAlStock()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + itemDto.getCantidadADevolver());
            }
        }

        boolean totalmenteDevuelto = pedido.getDetalles().stream()
            .allMatch(d -> d.getCantidad() == (d.getCantidadDevuelta() != null ? d.getCantidadDevuelta() : 0));
        
        pedido.setEstado(totalmenteDevuelto ? "DEVUELTO" : "DEVOLUCION_PARCIAL");
        Pedido updatedPedido = pedidoRepository.save(pedido);
        
        // Aquí se integraría la lógica para iniciar un reembolso en Mercado Pago usando el ID del pago
        
        return modelMapper.map(updatedPedido, OrderResponse.class);
    }

    @Override
    public byte[] generateInvoice(Long orderId, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));
        
        // 1. Verificar que el usuario tiene permiso para ver este pedido
        checkOrderAccess(pedido, usuario);

        // --- 2. ¡LÓGICA AÑADIDA! Verificar el estado del pedido ---
        String estado = pedido.getEstado();
        // Permitimos la descarga si el pedido está en cualquiera de estos estados post-pago.
        if (!("PAGADO".equalsIgnoreCase(estado) ||
              "PROCESANDO".equalsIgnoreCase(estado) || 
              "ENVIADO".equalsIgnoreCase(estado) || 
              "ENTREGADO".equalsIgnoreCase(estado))) {
            
            // Si no está en un estado válido, lanzamos una excepción.
            throw new IllegalStateException("No se puede generar la boleta para un pedido que no ha sido pagado. Estado actual: " + estado);
        }

        // 3. Si todo está bien, generar y devolver el PDF
        return pdfService.generateOrderInvoicePdf(pedido);
    }
    @Override
    public List<OrderResponse> findAll() {
        return pedidoRepository.findAll().stream()
                .map(p -> modelMapper.map(p, OrderResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> findByUsuario(Usuario usuario) {
        return pedidoRepository.findByCliente(usuario).stream()
                .map(p -> modelMapper.map(p, OrderResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse findByIdForUser(Long orderId, Usuario usuario) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));
        checkOrderAccess(pedido, usuario);
        return modelMapper.map(pedido, OrderResponse.class);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        Pedido pedido = pedidoRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado."));
        pedido.setEstado(status);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        return modelMapper.map(updatedPedido, OrderResponse.class);
    }
    
    // --- Métodos Privados de Ayuda ---

    private void updateStock(List<CarritoItem> items) {
        logger.info("Actualizando stock para {} items.", items.size());
        for (CarritoItem item : items) {
            // Obtenemos el producto directamente del item del carrito, ya está cargado.
            Producto producto = item.getProducto();
            int newStock = producto.getStock() - item.getCantidad();
            if (newStock < 0) {
                throw new IllegalStateException("Stock insuficiente para: " + producto.getNombre());
            }
            producto.setStock(newStock);
            // NO es necesario llamar a productoRepository.save(producto) aquí.
            // Como 'producto' es una entidad gestionada por la transacción,
            // Hibernate detectará el cambio en 'stock' y generará el UPDATE al final.
        }
    }

    private BigDecimal calculateTotal(List<CarritoItem> items) {
        return items.stream()
                .map(item -> item.getProducto().getPrecio().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void checkOrderAccess(Pedido pedido, Usuario usuario) {
        boolean isAdminOrWorker = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_WORKER"));
        
        if (!isAdminOrWorker && !pedido.getCliente().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No tienes permiso para acceder a este recurso.");
        }
    }
}