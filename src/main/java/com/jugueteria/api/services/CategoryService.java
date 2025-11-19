package com.jugueteria.api.services;
import java.util.List;
import com.jugueteria.api.dto.response.CategoryResponse;
import com.jugueteria.api.dto.request.CategoryRequest;
import com.jugueteria.api.entity.Categoria;
public interface CategoryService {
    List<CategoryResponse> findAll();
    CategoryResponse findById(Long id);
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void deleteById(Long id);
}