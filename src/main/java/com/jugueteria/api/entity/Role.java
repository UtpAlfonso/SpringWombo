package com.jugueteria.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Anotación de Lombok: genera getters, setters, toString, equals y hashCode.
@NoArgsConstructor // <-- AÑADIR: Crea el constructor new Role()
@AllArgsConstructor // <-- AÑADIR: Crea el constructor new Role(Integer id, String nombre)
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20, unique = true, nullable = false)
    private String nombre;

     public Role(String nombre) {
        this.nombre = nombre;
    }
}