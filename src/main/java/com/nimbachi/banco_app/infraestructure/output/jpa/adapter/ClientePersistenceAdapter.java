package com.nimbachi.banco_app.infraestructure.output.jpa.adapter;

import com.nimbachi.banco_app.appication.output.IClientePersistencePort;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.output.jpa.entity.ClienteEntity;
import com.nimbachi.banco_app.infraestructure.output.jpa.mapper.IClienteEntityMapper;
import com.nimbachi.banco_app.infraestructure.output.jpa.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientePersistenceAdapter implements IClientePersistencePort {

    private final ClienteRepository clienteRepository;
    private final IClienteEntityMapper clienteEntityMapper;

    @Override
    public Cliente save(Cliente cliente) {
        log.debug("Persistiendo cliente con ID: {}", cliente.getId());
        ClienteEntity entity = clienteEntityMapper.toEntity(cliente);
        ClienteEntity saved = clienteRepository.save(entity);
        return clienteEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        log.debug("Buscando cliente por ID: {}", id);
        return clienteRepository.findById(id)
                .map(clienteEntityMapper::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        log.debug("Obteniendo todos los clientes");
        return clienteRepository.findAll().stream()
                .map(clienteEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Cliente> findAllActive() {
        log.debug("Obteniendo clientes activos");
        return clienteRepository.findAll().stream()
                .filter(entity -> entity.getEstado() == true)
                .map(clienteEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Cliente> findByClienteId(String clienteId) {
        log.debug("Buscando cliente por clienteId: {}", clienteId);
        return clienteRepository.findByClienteId(clienteId)
                .map(clienteEntityMapper::toDomain);
    }

    @Override
    public boolean existsByClienteId(String clienteId) {
        log.debug("Validando existencia de clienteId: {}", clienteId);
        return clienteRepository.existsByClienteId(clienteId);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Eliminando cliente con ID: {}", id);
        clienteRepository.deleteById(id);
    }
}