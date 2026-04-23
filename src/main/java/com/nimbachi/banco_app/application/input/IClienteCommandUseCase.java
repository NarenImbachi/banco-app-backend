package com.nimbachi.banco_app.application.input;

import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;

public interface IClienteCommandUseCase {
    ClienteResponse crearCliente(Cliente cliente);
    ClienteResponse actualizar(Long id, Cliente cliente);
    void eliminar(Long id);
}
