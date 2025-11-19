package com.jugueteria.api.services;
import com.jugueteria.api.dto.request.ReviewRequest;
import com.jugueteria.api.dto.response.ReviewResponse;
import com.jugueteria.api.entity.Usuario;
import com.jugueteria.api.repository.ResenaRepository;
import java.util.List;
public interface ReviewService {
    List<ReviewResponse> findByProductId(Long productId);
    ReviewResponse create(Usuario usuario, Long productId, ReviewRequest request);
    List<ReviewResponse> findAll();
}