package com.jugueteria.api.services.impl;

import com.jugueteria.api.dto.request.ProductRequest;
import com.jugueteria.api.dto.response.ProductResponse;
import com.jugueteria.api.entity.Categoria;
import com.jugueteria.api.entity.Producto;
import com.jugueteria.api.entity.Proveedor;
import com.jugueteria.api.exception.ResourceNotFoundException;
import com.jugueteria.api.repository.CategoriaRepository;
import com.jugueteria.api.repository.ProductoRepository;
import com.jugueteria.api.repository.ProveedorRepository;
import com.jugueteria.api.services.FileStorageService; // Corregido el import
import com.jugueteria.api.services.ProductService;   // Corregido el import
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Override
    public List<ProductResponse> findAll() {
        return productoRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return convertToResponse(producto);
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request, MultipartFile file) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada."));
        
        Proveedor proveedor = (request.getProveedorId() != null) 
            ? proveedorRepository.findById(request.getProveedorId()).orElse(null) 
            : null;

        Producto producto = modelMapper.map(request, Producto.class);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        
        // --- LÓGICA DE IMAGEN CON CLOUDINARY ---
        if (file != null && !file.isEmpty()) {
            // 1. Guardar el archivo usando el servicio (que ahora es Cloudinary)
            String imageUrl = fileStorageService.save(file);
            // 2. Guardar la URL completa y segura que devuelve Cloudinary
            producto.setImageUrl(imageUrl);
        }
        
        Producto savedProducto = productoRepository.save(producto);
        return convertToResponse(savedProducto);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request, MultipartFile file) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado."));
        
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada."));
        
        Proveedor proveedor = (request.getProveedorId() != null) 
            ? proveedorRepository.findById(request.getProveedorId()).orElse(null) 
            : null;
        
        modelMapper.map(request, producto);
        producto.setId(id); // Aseguramos que el ID se mantenga
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        
        if (file != null && !file.isEmpty()) {
            String imageUrl = fileStorageService.save(file);
            producto.setImageUrl(imageUrl);
        }
        
        Producto updatedProducto = productoRepository.save(producto);
        return convertToResponse(updatedProducto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado.");
        }
        productoRepository.deleteById(id);
    }
    
    private ProductResponse convertToResponse(Producto producto) {
        ProductResponse response = modelMapper.map(producto, ProductResponse.class);
        if (producto.getCategoria() != null) {
            response.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        if (producto.getProveedor() != null) {
            response.setProveedorNombre(producto.getProveedor().getNombre());
        }
        return response;
    }
}