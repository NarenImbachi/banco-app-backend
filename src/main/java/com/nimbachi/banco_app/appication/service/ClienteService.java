package com.nimbachi.banco_app.appication.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbachi.banco_app.appication.input.IClienteCommandUseCase;
import com.nimbachi.banco_app.appication.input.IClienteQueryUseCase;
import com.nimbachi.banco_app.appication.output.IClientePersistencePort;
import com.nimbachi.banco_app.domain.model.Cliente;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService implements IClienteCommandUseCase, IClienteQueryUseCase {

    private final IClientePersistencePort clientePersistencePort;

    @Override
    @Transactional
    public Cliente crearCliente(Cliente cliente) {
        log.info("Creando nuevo cliente: {}", cliente.getClienteId());

        // Validar que clienteId sea único
        if (clientePersistencePort.existsByClienteId(cliente.getClienteId())) {
            throw new RuntimeException("El cliente ID " + cliente.getClienteId() + " ya existe");
        }

        cliente.setEstado(true); // Activo por defecto

        Cliente clienteCreado = clientePersistencePort.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", clienteCreado.getId());

        return clienteCreado;
    }

    @Override
    @Transactional
    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        log.info("Actualizando cliente con ID: {}", id);

        Cliente clienteExistente = clientePersistencePort.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));

        // Validar que no cambien identificación
        if (!clienteExistente.getIdentificacion().equals(clienteActualizado.getIdentificacion())) 
            throw new RuntimeException("No se puede cambiar la identificación del cliente");

        if (!clienteExistente.getClienteId().equals(clienteActualizado.getClienteId())) 
            throw new RuntimeException("No se puede cambiar el clienteId");
        

        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setGenero(clienteActualizado.getGenero());
        clienteExistente.setEdad(clienteActualizado.getEdad());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setEstado(clienteActualizado.getEstado());

        Cliente clienteActualizado_ = clientePersistencePort.save(clienteExistente);
        log.info("Cliente actualizado exitosamente");

        return clienteActualizado_;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando cliente con ID: {}", id);
        clientePersistencePort.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerPorId(Long id) {
        log.info("Obteniendo cliente por ID: {}", id);
        return clientePersistencePort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarActivos() {
        log.info("Listando clientes activos");
        return clientePersistencePort.findAllActive();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        log.info("Listando todos los clientes");
        return clientePersistencePort.findAll();
    }

    
    
}
