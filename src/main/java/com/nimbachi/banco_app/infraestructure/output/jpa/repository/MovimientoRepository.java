package com.nimbachi.banco_app.infraestructure.output.jpa.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nimbachi.banco_app.infraestructure.output.jpa.entity.MovimientoEntity;

@Repository
public interface MovimientoRepository extends JpaRepository<MovimientoEntity, Long> {
    List<MovimientoEntity> findByCuentaId(Long cuentaId);
    List<MovimientoEntity> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin);
    List<MovimientoEntity> findByCuentaIdAndFecha(Long cuentaId, LocalDate fecha);
}
