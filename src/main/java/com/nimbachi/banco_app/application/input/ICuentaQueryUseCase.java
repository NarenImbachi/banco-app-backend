package com.nimbachi.banco_app.application.input;

import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;

public interface ICuentaQueryUseCase {
    Optional<Cuenta> obtenerPorId(Long id);
    List<CuentaResponse> listarTodas();
}
