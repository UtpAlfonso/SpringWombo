package com.jugueteria.api.controller;

import com.jugueteria.api.dto.request.ProductRequest;
import com.jugueteria.api.dto.response.ProductResponse;
import com.jugueteria.api.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.multipart.MultipartFile; 

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        System.out.println(">>> Petición GET a /api/v1/products recibida <<<");
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }) // <-- 1. Especificar que se consume multipart
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestPart("product") ProductRequest request, // <-- 2. Obtener el JSON
            @RequestPart(value = "file", required = false) MultipartFile file // <-- 3. Obtener el archivo (opcional)
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request, file));
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }) // <-- 1. Especificar que se consume multipart
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestPart("product") ProductRequest request, // <-- 2. Obtener el JSON
            @RequestPart(value = "file", required = false) MultipartFile file // <-- 3. Obtener el archivo (opcional al actualizar)
    ) {
        return ResponseEntity.ok(productService.update(id, request, file));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}