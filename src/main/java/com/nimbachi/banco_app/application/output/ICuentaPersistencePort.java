package com.nimbachi.banco_app.application.output;

import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.domain.model.Cuenta;

public interface ICuentaPersistencePort {
    Cuenta save(Cuenta cuenta);
    Optional<Cuenta> findById(Long id);
    List<Cuenta> findAll();
    List<Cuenta> findByClienteId(Long clienteId);
    Optional<Cuenta> findByNumeroCuenta(String numeroCuenta);
    void delete(Long id);
    boolean existsByNumeroCuenta(String numeroCuenta);
}
