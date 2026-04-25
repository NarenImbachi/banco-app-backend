package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nimbachi.banco_app.application.input.ICuentaCommandUseCase;
import com.nimbachi.banco_app.application.input.ICuentaQueryUseCase;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateCuentaRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.UpdateCuentaRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ApiResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.ICuentaRestMapper;

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
    private final ICuentaRestMapper cuentaRestMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<CuentaResponse>> crearCuenta(@Valid @RequestBody CreateCuentaRequest request) {
        log.info("POST /api/cuentas - Creando nueva cuenta: {}", request.getNumeroCuenta());

        Cuenta cuenta = cuentaRestMapper.requestToDomain(request);

        CuentaResponse cuentaCreada = cuentaCommandUseCase.crearCuenta(cuenta);
        return new ResponseEntity<>(ApiResponse.success(cuentaCreada, "Cuenta creada exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cuenta>> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/cuentas/{} - Obteniendo cuenta", id);
        Optional<Cuenta> cuenta = cuentaQueryUseCase.obtenerPorId(id);

        if (cuenta.isEmpty()) {
            return new ResponseEntity<>(
                    ApiResponse.error("Cuenta no encontrada", "CUENTA_NOT_FOUND", HttpStatus.NOT_FOUND,
                            "/api/cuentas/" + id),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ApiResponse.success(cuenta.get()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CuentaResponse>>> listarTodas() {
        log.info("GET /api/cuentas - Listando todas las cuentas");
        List<CuentaResponse> cuentas = cuentaQueryUseCase.listarTodas();
        return new ResponseEntity<>(ApiResponse.success(cuentas, "Cuentas obtenidas exitosamente"), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CuentaResponse>> actualizar(@PathVariable Long id,
            @Valid @RequestBody UpdateCuentaRequest request) {
        log.info("PUT /api/cuentas/{} - Actualizando cuenta", id);
        Cuenta datosParaActualizar = cuentaRestMapper.updateRequestToDomain(request);
        CuentaResponse cuentaActualizada = cuentaCommandUseCase.actualizar(id, datosParaActualizar);

        return new ResponseEntity<>(ApiResponse.success(cuentaActualizada, "Cuenta actualizada exitosamente"),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/cuentas/{} - Eliminando cuenta", id);
        Optional<Cuenta> cuentaOptional = cuentaQueryUseCase.obtenerPorId(id);

        if (cuentaOptional.isEmpty()) {
            return new ResponseEntity<>(
                    ApiResponse.error("Cuenta no encontrada", "CUENTA_NOT_FOUND", HttpStatus.NOT_FOUND,
                            "/api/cuentas/" + id),
                    HttpStatus.NOT_FOUND);
        }

        cuentaCommandUseCase.eliminar(id);
        return new ResponseEntity<>(ApiResponse.successEmpty("Cuenta eliminada exitosamente", "CUENTA_DELETED"),
                HttpStatus.OK);
    }
}