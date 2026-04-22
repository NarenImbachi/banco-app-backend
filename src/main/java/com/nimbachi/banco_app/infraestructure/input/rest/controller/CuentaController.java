package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nimbachi.banco_app.appication.input.ICuentaCommandUseCase;
import com.nimbachi.banco_app.appication.input.ICuentaQueryUseCase;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateCuentaRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.UpdateCuentaRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
@Slf4j
public class CuentaController {

    private final ICuentaCommandUseCase cuentaCommandUseCase;
    private final ICuentaQueryUseCase cuentaQueryUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<Cuenta>> crearCuenta(@Valid @RequestBody CreateCuentaRequest request) {
        log.info("POST /api/cuentas - Creando nueva cuenta: {}", request.getNumeroCuenta());
        
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipo(request.getTipo());
        cuenta.setSaldoInicial(request.getSaldoInicial());
        cuenta.setEstado(request.isEstado());
        cuenta.setClienteId(request.getClienteId());

        Cuenta cuentaCreada = cuentaCommandUseCase.crearCuenta(cuenta);
        return new ResponseEntity<>(ApiResponse.success(cuentaCreada, "Cuenta creada exitosamente"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cuenta>> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/cuentas/{} - Obteniendo cuenta", id);
        Optional<Cuenta> cuenta = cuentaQueryUseCase.obtenerPorId(id);
        
        if (cuenta.isEmpty()) {
            return new ResponseEntity<>(
                ApiResponse.error("Cuenta no encontrada", "CUENTA_NOT_FOUND", HttpStatus.NOT_FOUND, "/api/cuentas/" + id),
                HttpStatus.NOT_FOUND
            );
        }
        
        return new ResponseEntity<>(ApiResponse.success(cuenta.get()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cuenta>>> listarTodas() {
        log.info("GET /api/cuentas - Listando todas las cuentas");
        List<Cuenta> cuentas = cuentaQueryUseCase.listarTodas();
        return new ResponseEntity<>(ApiResponse.success(cuentas, "Cuentas obtenidas exitosamente"), HttpStatus.OK);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<List<Cuenta>>> obtenerPorCliente(@PathVariable Long clienteId) {
        log.info("GET /api/cuentas/cliente/{} - Obteniendo cuentas del cliente", clienteId);
        List<Cuenta> cuentas = cuentaQueryUseCase.obtenerPorCliente(clienteId);
        return new ResponseEntity<>(ApiResponse.success(cuentas, "Cuentas del cliente obtenidas exitosamente"), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cuenta>> actualizar(@PathVariable Long id, @Valid @RequestBody UpdateCuentaRequest request) {
        Optional<Cuenta> cuentaOptional = cuentaQueryUseCase.obtenerPorId(id);
        
        if (cuentaOptional.isEmpty()) {
            return new ResponseEntity<>(
                ApiResponse.error("Cuenta no encontrada", "CUENTA_NOT_FOUND", HttpStatus.NOT_FOUND, "/api/cuentas/" + id),
                HttpStatus.NOT_FOUND
            );
        }

        Cuenta cuenta = cuentaOptional.get();
        if (request.getTipo() != null) cuenta.setTipo(request.getTipo());
        if (request.isEstado()) cuenta.setEstado(request.isEstado());

        Cuenta cuentaActualizada = cuentaCommandUseCase.actualizar(cuenta.getId(), cuenta);
        return new ResponseEntity<>(ApiResponse.success(cuentaActualizada, "Cuenta actualizada exitosamente"), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/cuentas/{} - Eliminando cuenta", id);
        Optional<Cuenta> cuentaOptional = cuentaQueryUseCase.obtenerPorId(id);
        
        if (cuentaOptional.isEmpty()) {
            return new ResponseEntity<>(
                ApiResponse.error("Cuenta no encontrada", "CUENTA_NOT_FOUND", HttpStatus.NOT_FOUND, "/api/cuentas/" + id),
                HttpStatus.NOT_FOUND
            );
        }

        cuentaCommandUseCase.eliminar(id);
        return new ResponseEntity<>(ApiResponse.successEmpty("Cuenta eliminada exitosamente", "CUENTA_DELETED"), HttpStatus.OK);
    }
}