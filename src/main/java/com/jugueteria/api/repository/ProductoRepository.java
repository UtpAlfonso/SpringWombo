package com.jugueteria.api.repository;

import com.jugueteria.api.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Spring Data JPA provee findAll, findById, save, deleteById, etc. automáticamente.
    // Se pueden añadir métodos de búsqueda personalizados más adelante si es necesario.
    // Ejemplo: List<Producto> findByCategoriaId(Long categoriaId);
}