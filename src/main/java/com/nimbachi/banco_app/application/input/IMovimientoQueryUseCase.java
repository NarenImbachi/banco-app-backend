package com.nimbachi.banco_app.application.input;

import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;

public interface IMovimientoQueryUseCase {
    Optional<Movimiento> obtenerPorId(Long id);
    List<MovimientoListadoResponse> listarMovimientosFormateados();
}
