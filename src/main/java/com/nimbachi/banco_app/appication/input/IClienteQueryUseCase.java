package com.nimbachi.banco_app.appication.input;

import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;

public interface IClienteQueryUseCase {
    Optional<Cliente> obtenerPorId(Long id);
    List<Cliente> listarActivos();
    List<ClienteResponse> listarTodos();
}