package com.jugueteria.api.repository;

import com.jugueteria.api.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    /**
     * Busca una promoción por su código único.
     *
     * @param codigo El código de la promoción (ej. "VERANO2025").
     * @return un Optional que contiene la Promocion si se encuentra.
     */
    Optional<Promocion> findByCodigo(String codigo);
}