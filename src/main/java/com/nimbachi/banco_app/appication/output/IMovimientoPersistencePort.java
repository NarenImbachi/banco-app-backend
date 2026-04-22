package com.nimbachi.banco_app.appication.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Movimiento;

public interface IMovimientoPersistencePort {
    Movimiento save(Movimiento movimiento);
    Optional<Movimiento> findById(Long id);
    List<Movimiento> findAll();
    List<Movimiento> findByCuentaId(Long cuentaId);
    List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin);
    List<Movimiento> findByCuentaIdAndFecha(Long cuentaId, LocalDate fecha);
    void delete(Long id);
}