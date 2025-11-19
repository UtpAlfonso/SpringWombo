package com.jugueteria.api.services.impl;

import com.jugueteria.api.services.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path root = Paths.get("uploads");

    @Override
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta para las subidas de archivos!", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(uniqueFilename));
            
            // Construir la URL completa para acceder al archivo localmente
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/files/")
                    .path(uniqueFilename)
                    .toUriString();
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo localmente: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error al formar la URL del recurso: " + e.getMessage());
        }
    }
}