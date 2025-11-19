package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.request.ReviewRequest;
import com.jugueteria.api.dto.response.ReviewResponse;
import com.jugueteria.api.entity.Producto;
import com.jugueteria.api.entity.Resena;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.ProductoRepository;
import com.jugueteria.api.repository.ResenaRepository;
import com.jugueteria.api.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ResenaRepository resenaRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ReviewResponse> findByProductId(Long productId) {
        if (!productoRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + productId);
        }
        return resenaRepository.findByProductoId(productId).stream()
                .map(resena -> modelMapper.map(resena, ReviewResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponse create(Usuario usuario, Long productId, ReviewRequest request) {
        Producto producto = productoRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + productId));

        Resena resena = new Resena();
        resena.setProducto(producto);
        resena.setNombreUsuario(usuario.getNombre() + " " + usuario.getApellido()); // Denormalización
        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());

        Resena savedResena = resenaRepository.save(resena);
        return modelMapper.map(savedResena, ReviewResponse.class);
    }

    @Override
    public List<ReviewResponse> findAll() {
        // Ordenamos por fecha de creación descendente para obtener las más recientes primero
        return resenaRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::convertToResponse) // Usamos un helper para el mapeo
                .collect(Collectors.toList());
    }
    
    // ... (método findByProductId también debe usar convertToResponse)

    // ... (método create)

    // --- MÉTODO HELPER PARA EL MAPEO ---
    private ReviewResponse convertToResponse(Resena resena) {
        ReviewResponse response = modelMapper.map(resena, ReviewResponse.class);
        if (resena.getProducto() != null) {
            response.setProductoNombre(resena.getProducto().getNombre());
            response.setProductoImageUrl(resena.getProducto().getImageUrl());
        }
        return response;
    }
}