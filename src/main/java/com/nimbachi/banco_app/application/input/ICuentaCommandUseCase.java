package com.nimbachi.banco_app.application.input;

import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;

public interface ICuentaCommandUseCase {
    CuentaResponse crearCuenta(Cuenta cuenta);
    CuentaResponse actualizar(Long id, Cuenta cuenta);
    void eliminar(Long id);
}