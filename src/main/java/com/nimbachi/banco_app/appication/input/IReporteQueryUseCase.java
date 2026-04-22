package com.nimbachi.banco_app.appication.input;

import java.time.LocalDate;

import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.EstadoCuentaResponse;

public interface IReporteQueryUseCase {
    EstadoCuentaResponse generarReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
