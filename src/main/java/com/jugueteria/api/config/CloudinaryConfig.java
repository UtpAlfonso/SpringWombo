package com.jugueteria.api.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    /**
     * Crea un Bean de Cloudinary.
     * El SDK buscará automáticamente la variable de entorno 'CLOUDINARY_URL'
     * para configurarse. No necesitamos pasarle las credenciales manualmente.
     * @return una instancia de Cloudinary lista para usar.
     */
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary();
    }
}