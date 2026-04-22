package com.nimbachi.banco_app.appication.input;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Movimiento;

public interface IMovimientoQueryUseCase {
    Optional<Movimiento> obtenerPorId(Long id);
    List<Movimiento> obtenerPorCuenta(Long cuentaId);
    List<Movimiento> obtenerPorRangoFechas(Long cuentaId, LocalDate fechaInicio, LocalDate fechaFin);
    List<Movimiento> obtenerMovimientosPorCliente(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
