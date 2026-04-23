package com.nimbachi.banco_app.application.input;

import java.time.LocalDate;
import java.util.List;

import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ReporteMovimientoResponse;

public interface IReporteQueryUseCase {
    List<ReporteMovimientoResponse> generarReporteEstadoCuenta( Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
