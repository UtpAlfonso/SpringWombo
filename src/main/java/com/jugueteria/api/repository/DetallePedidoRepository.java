package com.jugueteria.api.repository;

import com.jugueteria.api.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad DetallePedido.
 * Proporciona operaciones CRUD básicas y búsquedas específicas para los detalles de los pedidos.
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    // Spring Data JPA provee automáticamente métodos como:
    // - save(DetallePedido entity): Guarda un detalle de pedido.
    // - findById(Long id): Busca un detalle de pedido por su ID.
    // - findAll(): Obtiene todos los detalles de pedidos.
    // - deleteById(Long id): Elimina un detalle de pedido por su ID.

    // Puedes añadir métodos de búsqueda personalizados aquí si los necesitas en el futuro.
    // Por ejemplo:
    // List<DetallePedido> findByPedidoId(Long pedidoId);
    // Optional<DetallePedido> findByPedidoIdAndProductoId(Long pedidoId, Long productoId);
}