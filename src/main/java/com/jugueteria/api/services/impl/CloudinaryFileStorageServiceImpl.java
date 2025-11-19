package com.jugueteria.api.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jugueteria.api.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;

@Service
@Primary // ¡Muy Importante! Le dice a Spring que prefiera esta implementación sobre la local.
@RequiredArgsConstructor
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String save(MultipartFile file) {
        try {
            // Sube el archivo a Cloudinary. No necesitamos especificar ninguna opción por ahora.
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            
            // Cloudinary nos devuelve mucha información. Extraemos la URL segura (https).
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            // Si la subida falla, lanzamos una excepción.
            throw new RuntimeException("No se pudo subir el archivo a Cloudinary", e);
        }
    }

    @Override
    public Resource load(String filename) {
        // Esta implementación no carga archivos locales. Cloudinary los sirve directamente.
        // Este método no debería ser llamado si Cloudinary está configurado.
        throw new UnsupportedOperationException("La carga de recursos locales no está soportada en la configuración de Cloudinary.");
    }
    // Los otros métodos de la interfaz no son necesarios para el flujo de subida de Cloudinary,
    // así que los dejamos con una implementación vacía.
    @Override
    public void init() {
        // No se necesita inicializar carpetas locales.
    }
    
    // ... (implementaciones vacías para 'load', 'deleteAll', etc.)
}
