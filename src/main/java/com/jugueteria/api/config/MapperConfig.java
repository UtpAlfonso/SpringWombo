package com.jugueteria.api.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jugueteria.api.dto.response.ProductResponse;
import com.jugueteria.api.entity.Producto;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        // Creamos una nueva instancia de ModelMapper
        ModelMapper modelMapper = new ModelMapper();

        // --- ESTA ES LA CONFIGURACIÓN CRUCIAL ---
        // Le decimos a ModelMapper que use una estrategia de "matching" estricta.
        // Esto significa que solo mapeará `source.field` a `destination.field` si
        // los nombres son idénticos.
        // Previene el error de ambigüedad con 'categoriaId' -> 'id'.
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(Producto.class, ProductResponse.class)
            .setPostConverter(context -> {
                Producto source = context.getSource(); // La entidad Producto
                ProductResponse destination = context.getDestination(); // El DTO ProductResponse

                // Asignar manualmente los nombres de las entidades relacionadas
                if (source.getCategoria() != null) {
                    destination.setCategoriaNombre(source.getCategoria().getNombre());
                }
                if (source.getProveedor() != null) {
                    destination.setProveedorNombre(source.getProveedor().getNombre());
                }
                
                // El campo imageUrl se mapeará correctamente de forma automática porque
                // ambos son de tipo String. No se necesita lógica especial para él.

                return destination;
            });
        return modelMapper;
    }
}