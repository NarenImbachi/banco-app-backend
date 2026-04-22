package com.nimbachi.banco_app.appication.input;

import com.nimbachi.banco_app.domain.model.Cuenta;

public interface ICuentaCommandUseCase {
    Cuenta crearCuenta(Cuenta cuenta);
    Cuenta actualizar(Long id, Cuenta cuenta);
    void eliminar(Long id);
}