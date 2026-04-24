package com.nimbachi.banco_app.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbachi.banco_app.application.input.IClienteCommandUseCase;
import com.nimbachi.banco_app.application.input.IClienteQueryUseCase;
import com.nimbachi.banco_app.application.output.IClientePersistencePort;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IClienteRestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService implements IClienteCommandUseCase, IClienteQueryUseCase {

    private final IClientePersistencePort clientePersistencePort;
    private final IClienteRestMapper clienteRestMapper;

    @Override
    @Transactional
    public ClienteResponse crearCliente(Cliente cliente) {
        log.info("Creando nuevo cliente: {}", cliente.getClienteId());
        if (clientePersistencePort.existsByClienteId(cliente.getClienteId())) 
            throw new RuntimeException("El cliente ID " + cliente.getClienteId() + " ya existe");
        

        Cliente nuevoCliente = Cliente.crearCliente(
            cliente.getClienteId(), 
            cliente.getContrasena(), 
            cliente.getNombre(), 
            cliente.getGenero(), 
            cliente.getEdad(), 
            cliente.getIdentificacion(), 
            cliente.getDireccion(), 
            cliente.getTelefono()
        );

        Cliente clienteCreado = clientePersistencePort.save(nuevoCliente);
        log.info("Cliente creado exitosamente con ID: {}", clienteCreado.getId());

        return clienteRestMapper.domainToResponse(clienteCreado);
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Long id, Cliente clienteActualizado) {
        log.info("Actualizando cliente con ID: {}", id);

        Cliente clienteExistente = clientePersistencePort.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente con ID " + id + " no encontrado"));

        // Validar que no cambien identificación
        if (!clienteExistente.getIdentificacion().equals(clienteActualizado.getIdentificacion()))
            throw new RuntimeException("No se puede cambiar la identificación del cliente");

        if (!clienteExistente.getClienteId().equals(clienteActualizado.getClienteId()))
            throw new RuntimeException("No se puede cambiar el clienteId");

        clienteExistente.actualizarDatos(
            clienteActualizado.getNombre(),
            clienteActualizado.getGenero(),
            clienteActualizado.getEdad(),
            clienteActualizado.getDireccion(),
            clienteActualizado.getTelefono(),
            clienteActualizado.isEstado()
        );

        Cliente clienteGuardado = clientePersistencePort.save(clienteExistente);
        log.info("Cliente actualizado exitosamente");

        return clienteRestMapper.domainToResponse(clienteGuardado);
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
    public List<ClienteResponse> listarTodos() {
        log.info("Listando todos los clientes");
        List<Cliente> clientes = clientePersistencePort.findAll();
        return clienteRestMapper.domainListToResponseList(clientes);
    }
}
