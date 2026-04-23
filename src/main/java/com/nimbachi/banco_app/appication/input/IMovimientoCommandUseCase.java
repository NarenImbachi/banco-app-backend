package com.nimbachi.banco_app.appication.input;

import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoResponse;

public interface IMovimientoCommandUseCase {
    MovimientoResponse registrarMovimiento(Long cuentaId, Movimiento movimiento);
    void eliminar(Long id);
}
