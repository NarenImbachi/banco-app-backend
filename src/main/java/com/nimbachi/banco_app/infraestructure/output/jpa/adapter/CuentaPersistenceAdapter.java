package com.nimbachi.banco_app.infraestructure.output.jpa.adapter;

import com.nimbachi.banco_app.appication.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.CuentaEntity;
import com.nimbachi.banco_app.infraestructure.output.jpa.mapper.ICuentaEntityMapper;
import com.nimbachi.banco_app.infraestructure.output.jpa.repository.CuentaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CuentaPersistenceAdapter implements ICuentaPersistencePort {

    private final CuentaRepository cuentaRepository;
    private final ICuentaEntityMapper cuentaEntityMapper;

    @Override
    public Cuenta save(Cuenta cuenta) {
        log.debug("Persistiendo cuenta: {}", cuenta.getNumeroCuenta());
        CuentaEntity entity = cuentaEntityMapper.toEntity(cuenta);
        CuentaEntity saved = cuentaRepository.save(entity);
        return cuentaEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Cuenta> findById(Long cuentaId) {
        log.debug("Buscando cuenta por ID: {}", cuentaId);
        return cuentaRepository.findById(cuentaId)
                .map(cuentaEntityMapper::toDomain);
    }

    @Override
    public List<Cuenta> findAll() {
        log.debug("Obteniendo todas las cuentas");
        return cuentaRepository.findAll().stream()
                .map(cuentaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Cuenta> findByClienteId(Long clienteId) {
        log.debug("Obteniendo cuentas del cliente ID: {}", clienteId);
        return cuentaRepository.findByClienteId(clienteId).stream()
                .map(cuentaEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        log.debug("Buscando cuenta por número: {}", numeroCuenta);
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .map(cuentaEntityMapper::toDomain);
    }

    @Override
    public boolean existsByNumeroCuenta(String numeroCuenta) {
        log.debug("Validando existencia de número de cuenta: {}", numeroCuenta);
        return cuentaRepository.existsByNumeroCuenta(numeroCuenta);
    }

    @Override
    public void delete(Long cuentaId) {
        log.debug("Eliminando cuenta con ID: {}", cuentaId);
        cuentaRepository.deleteById(cuentaId);
    }
}