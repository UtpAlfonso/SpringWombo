package com.jugueteria.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

      @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:4200",
                    "https://hyperlogical-luz-subfastigiate.ngrok-free.dev",
                    "http://hyperlogical-luz-subfastigiate.ngrok-free.dev",
                    "https://jugueteria-api.onrender.com",
                    "https://womboangular.onrender.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Llama a la configuración por defecto para que no se pierda (ej. para Swagger UI)
        WebMvcConfigurer.super.addResourceHandlers(registry);
        
        // --- ESTA ES LA CONFIGURACIÓN CLAVE ---
        
        // Cuando una petición llegue a la URL que empiece con "/uploads/files/**"
        registry.addResourceHandler("/uploads/files/**")
                
                // Sírvela desde la siguiente ubicación física en el disco duro:
                // "file:uploads/" significa la carpeta "uploads" en la raíz del proyecto.
                .addResourceLocations("file:uploads/");
    }
}