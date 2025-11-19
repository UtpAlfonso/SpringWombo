package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.request.CategoryRequest;
import com.jugueteria.api.dto.response.CategoryResponse;
import com.jugueteria.api.entity.Categoria;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.CategoriaRepository;
import com.jugueteria.api.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoriaRepository categoriaRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CategoryResponse> findAll() {
        return categoriaRepository.findAll().stream()
                .map(cat -> modelMapper.map(cat, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        return modelMapper.map(categoria, CategoryResponse.class);
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Categoria categoria = modelMapper.map(request, Categoria.class);
        Categoria savedCategoria = categoriaRepository.save(categoria);
        return modelMapper.map(savedCategoria, CategoryResponse.class);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        categoria.setNombre(request.getNombre());
        Categoria updatedCategoria = categoriaRepository.save(categoria);
        return modelMapper.map(updatedCategoria, CategoryResponse.class);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar, categoría no encontrada con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}