package com.jugueteria.api.services;
import com.jugueteria.api.dto.request.ProductRequest;
import com.jugueteria.api.dto.response.ProductResponse;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
public interface ProductService {
    List<ProductResponse> findAll();
    ProductResponse findById(Long id);
    ProductResponse create(ProductRequest request, MultipartFile file);
    ProductResponse update(Long id, ProductRequest request, MultipartFile file);
    void deleteById(Long id);
}
