package com.jugueteria.api.repository;

import com.jugueteria.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Busca un rol por su nombre único.
     *
     * @param nombre El nombre del rol a buscar (ej. "ROLE_ADMIN").
     * @return un Optional que contiene al Rol si se encuentra.
     */
    Optional<Role> findByNombre(String nombre);
}