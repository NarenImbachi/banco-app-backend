package com.nimbachi.banco_app.appication.input;

import com.nimbachi.banco_app.domain.model.Cliente;

public interface IClienteCommandUseCase {
    Cliente crearCliente(Cliente cliente);
    Cliente actualizar(Long id, Cliente cliente);
    void eliminar(Long id);
}
