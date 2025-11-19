package com.jugueteria.api.repository;

import com.jugueteria.api.entity.CarritoItem;
import com.jugueteria.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    /**
     * Busca todos los items del carrito para un usuario específico.
     *
     * @param usuario El usuario cuyo carrito se quiere obtener.
     * @return una Lista de CarritoItem.
     */
    List<CarritoItem> findByUsuario(Usuario usuario);

    /**
     * Busca un item específico en el carrito de un usuario por el ID del producto.
     *
     * @param usuario El usuario.
     * @param productoId El ID del producto.
     * @return un Optional que contiene el CarritoItem si existe.
     */
    Optional<CarritoItem> findByUsuarioAndProductoId(Usuario usuario, Long productoId);

    /**
     * Elimina todos los items del carrito para un usuario específico.
     * Se usa después de que una compra se ha completado.
     *
     * @param usuario El usuario cuyo carrito se va a limpiar.
     */
    void deleteByUsuario(Usuario usuario);
}