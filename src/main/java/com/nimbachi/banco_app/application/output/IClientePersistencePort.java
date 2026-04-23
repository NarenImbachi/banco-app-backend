package com.nimbachi.banco_app.application.output;

import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Cliente;

public interface IClientePersistencePort {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    List<Cliente> findAll();
    Optional<Cliente> findByClienteId(String clienteId);
    void deleteById(Long id);
    boolean existsByClienteId(String clienteId);
}
