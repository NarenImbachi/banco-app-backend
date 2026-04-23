package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nimbachi.banco_app.appication.input.IClienteCommandUseCase;
import com.nimbachi.banco_app.appication.input.IClienteQueryUseCase;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.UpdateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ApiResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IClienteRestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final IClienteCommandUseCase clienteCommandUseCase;
    private final IClienteQueryUseCase clienteQueryUseCase;
    private final IClienteRestMapper clienteRestMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> crearCliente(@Valid @RequestBody CreateClienteRequest request) {
        log.info("POST /api/clientes - Creando nuevo cliente: {}", request.getClienteId());
        
        Cliente cliente = new Cliente();
        cliente = clienteRestMapper.requestToDomain(request);

        Cliente clienteCreado = clienteCommandUseCase.crearCliente(cliente);
        return new ResponseEntity<>(ApiResponse.success(clienteCreado, "Cliente creado exitosamente"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/clientes/{} - Obteniendo cliente", id);
        Optional<Cliente> cliente = clienteQueryUseCase.obtenerPorId(id);
        
        if (cliente.isEmpty()) {
            return new ResponseEntity<>(
                ApiResponse.error("Cliente no encontrado", "CLIENTE_NOT_FOUND", HttpStatus.NOT_FOUND, "/api/clientes/" + id),
                HttpStatus.NOT_FOUND
            );
        }
        
        return new ResponseEntity<>(ApiResponse.success(cliente.get()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listarTodos() {
        log.info("GET /api/clientes - Listando todos los clientes");
        List<ClienteResponse> clientes = clienteQueryUseCase.listarTodos();
        return new ResponseEntity<>(ApiResponse.success(clientes, "Clientes obtenidos exitosamente"), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> actualizar(@PathVariable Long id, @Valid @RequestBody UpdateClienteRequest request) {
        log.info("PUT /api/clientes/{} - Actualizando cliente", id);
        Optional<Cliente> clienteOptional = clienteQueryUseCase.obtenerPorId(id);
        
        if (clienteOptional.isEmpty()) {
            return new ResponseEntity<>(
                ApiResponse.error("Cliente no encontrado", "CLIENTE_NOT_FOUND", HttpStatus.NOT_FOUND, "/api/clientes/" + id),
                HttpStatus.NOT_FOUND
            );
        }

        Cliente cliente = clienteOptional.get();
        if (request.getNombre() != null) cliente.setNombre(request.getNombre());
        if (request.getDireccion() != null) cliente.setDireccion(request.getDireccion());
        if (request.getTelefono() != null) cliente.setTelefono(request.getTelefono());
        cliente.setEstado(request.isEstado());

        Cliente clienteActualizado = clienteCommandUseCase.actualizar(cliente.getId(), cliente);
        return new ResponseEntity<>(ApiResponse.success(clienteActualizado, "Cliente actualizado exitosamente"), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/clientes/{} - Eliminando cliente", id);
        Optional<Cliente> clienteOptional = clienteQueryUseCase.obtenerPorId(id);
        
        if (clienteOptional.isEmpty()) {
            return new ResponseEntity<>(
                ApiResponse.error("Cliente no encontrado", "CLIENTE_NOT_FOUND", HttpStatus.NOT_FOUND, "/api/clientes/" + id),
                HttpStatus.NOT_FOUND
            );
        }

        clienteCommandUseCase.eliminar(id);
        return new ResponseEntity<>(ApiResponse.successEmpty("Cliente eliminado exitosamente", "CLIENTE_DELETED"), HttpStatus.OK);
    }
}