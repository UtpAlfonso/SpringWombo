package com.jugueteria.api.repository;

import com.jugueteria.api.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    /**
     * Busca todas las reseñas asociadas a un producto específico.
     *
     * @param productoId El ID del producto cuyas reseñas se quieren obtener.
     * @return una Lista de Resenas.
     */
    List<Resena> findByProductoId(Long productoId);
}