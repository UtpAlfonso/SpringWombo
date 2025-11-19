package com.jugueteria.api.repository;

import com.jugueteria.api.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
     Optional<Pago> findByIdTransaccionExterna(String idTransaccionExterna);
}