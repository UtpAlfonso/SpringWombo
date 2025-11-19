package com.jugueteria.api.services;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.springframework.core.io.Resource;

public interface FileStorageService {
  String save(MultipartFile file);
  Resource load(String filename);

    /**
     * Método de inicialización (puede estar vacío para implementaciones en la nube).
     */
    void init();

}