package com.nimbachi.banco_app.appication.input;

import com.nimbachi.banco_app.domain.model.Movimiento;

public interface IMovimientoCommandUseCase {
    Movimiento registrarMovimiento(Long cuentaId, Movimiento movimiento);
    void eliminar(Long id);
}
