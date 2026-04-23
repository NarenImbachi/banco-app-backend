package com.nimbachi.banco_app.application.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;

public interface IMovimientoPersistencePort {
    Movimiento save(Movimiento movimiento);
    Optional<Movimiento> findById(Long id);
    List<Movimiento> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin);
    List<Movimiento> findByCuentaIdAndFecha(Long cuentaId, LocalDate fecha);
    void delete(Long id);
    List<MovimientoListadoResponse> obtenerListadoMovimientos();
}