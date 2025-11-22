package com.jugueteria.api.repository;

import com.jugueteria.api.dto.response.DailySaleDto;
import com.jugueteria.api.dto.response.ProductSaleDto;
import com.jugueteria.api.entity.Pedido;
import com.jugueteria.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock; 
import jakarta.persistence.LockModeType;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca todos los pedidos realizados por un cliente específico.
     *
     * @param cliente El usuario (con rol CLIENT) cuyos pedidos se quieren encontrar.
     * @return una Lista de Pedidos.
     * 
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Pedido p WHERE p.id = :id")
    Optional<Pedido> findByIdWithLock(@Param("id") Long id);

    List<Pedido> findByCliente(Usuario cliente);
    @Query("SELECT new com.jugueteria.api.dto.response.DailySaleDto(CAST(p.fechaPedido AS date), SUM(p.total)) " +
           "FROM Pedido p " +
           "WHERE p.fechaPedido BETWEEN :startDate AND :endDate AND p.estado NOT IN ('PAGO_FALLIDO', 'PENDIENTE', 'CANCELADO') " +
           "GROUP BY CAST(p.fechaPedido AS date) " +
           "ORDER BY CAST(p.fechaPedido AS date)")
    List<DailySaleDto> findSalesByDay(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Consulta que agrupa los detalles de pedido por producto para encontrar los más vendidos
     * en un rango de fechas.
     */
   @Query("SELECT new com.jugueteria.api.dto.response.ProductSaleDto(d.producto.nombre, SUM(d.cantidad), SUM(d.precioUnitario * d.cantidad)) " +
       "FROM DetallePedido d " +
           "WHERE d.pedido.fechaPedido BETWEEN :startDate AND :endDate AND d.pedido.estado <> 'PAGO_FALLIDO' AND d.pedido.estado <> 'PENDIENTE' " +
           "GROUP BY d.producto.nombre " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<ProductSaleDto> findTopSellingProducts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Cuenta el número de pedidos válidos en un rango de fechas.
     */
    long countByFechaPedidoBetweenAndEstadoNotIn(LocalDateTime startDate, LocalDateTime endDate, List<String> excludedStatuses);
}