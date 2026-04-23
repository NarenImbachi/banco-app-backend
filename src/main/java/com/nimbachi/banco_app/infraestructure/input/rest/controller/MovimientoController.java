package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nimbachi.banco_app.appication.input.IMovimientoCommandUseCase;
import com.nimbachi.banco_app.appication.input.IMovimientoQueryUseCase;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateMovimientoRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Slf4j
public class MovimientoController {

        private final IMovimientoCommandUseCase movimientoCommandUseCase;
        private final IMovimientoQueryUseCase movimientoQueryUseCase;

        @PostMapping
        public ResponseEntity<ApiResponse<Movimiento>> registrarMovimiento(
                        @Valid @RequestBody CreateMovimientoRequest request) {
                log.info("POST /api/movimientos - Registrando movimiento en cuenta: {}", request.getCuentaId());

                Movimiento movimiento = new Movimiento();
                movimiento.setTipo(request.getTipo());
                movimiento.setValor(request.getValor());
                movimiento.setCuentaId(request.getCuentaId());

                Movimiento movimientoRegistrado = movimientoCommandUseCase.registrarMovimiento(movimiento.getCuentaId(),
                                movimiento);
                return new ResponseEntity<>(
                                ApiResponse.success(movimientoRegistrado, "Movimiento registrado exitosamente"),
                                HttpStatus.CREATED);
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<Movimiento>>> obtenerTodos() {
                log.info("GET /api/movimientos - Obteniendo todos los movimientos");
                List<Movimiento> movimientos = movimientoQueryUseCase.obtenerTodos();
                return new ResponseEntity<>(ApiResponse.success(movimientos, "Movimientos obtenidos exitosamente"),
                                HttpStatus.OK);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<Movimiento>> obtenerPorId(@PathVariable Long id) {
                log.info("GET /api/movimientos/{} - Obteniendo movimiento", id);
                Optional<Movimiento> movimiento = movimientoQueryUseCase.obtenerPorId(id);

                if (movimiento.isEmpty()) {
                        return new ResponseEntity<>(
                                        ApiResponse.error("Movimiento no encontrado", "MOVIMIENTO_NOT_FOUND",
                                                        HttpStatus.NOT_FOUND,
                                                        "/api/movimientos/" + id),
                                        HttpStatus.NOT_FOUND);
                }

                return new ResponseEntity<>(ApiResponse.success(movimiento.get()), HttpStatus.OK);
        }

        @GetMapping("/cuenta/{cuentaId}")
        public ResponseEntity<ApiResponse<List<Movimiento>>> obtenerPorCuenta(@PathVariable Long cuentaId) {
                log.info("GET /api/movimientos/cuenta/{} - Obteniendo movimientos de la cuenta", cuentaId);
                List<Movimiento> movimientos = movimientoQueryUseCase.obtenerPorCuenta(cuentaId);
                return new ResponseEntity<>(ApiResponse.success(movimientos, "Movimientos obtenidos exitosamente"),
                                HttpStatus.OK);
        }

        @GetMapping("/cuenta/{cuentaId}/rango")
        public ResponseEntity<ApiResponse<List<Movimiento>>> obtenerPorRangoFechas(
                        @PathVariable Long cuentaId,
                        @RequestParam LocalDate fechaInicio,
                        @RequestParam LocalDate fechaFin) {
                log.info("GET /api/movimientos/cuenta/{}/rango - Obteniendo movimientos entre {} y {}", cuentaId,
                                fechaInicio,
                                fechaFin);
                List<Movimiento> movimientos = movimientoQueryUseCase.obtenerPorRangoFechas(cuentaId, fechaInicio,
                                fechaFin);
                return new ResponseEntity<>(
                                ApiResponse.success(movimientos, "Movimientos en rango obtenidos exitosamente"),
                                HttpStatus.OK);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
                log.info("DELETE /api/movimientos/{} - Intentando eliminar movimiento", id);

                Optional<Movimiento> movimientoOptional = movimientoQueryUseCase.obtenerPorId(id);
                if (movimientoOptional.isEmpty()) {
                        return new ResponseEntity<>(
                                ApiResponse.error("Movimiento no encontrado", "MOVIMIENTO_NOT_FOUND", HttpStatus.NOT_FOUND, "/api/movimientos/" + id),
                                HttpStatus.NOT_FOUND);
                }

                movimientoCommandUseCase.eliminar(id);
                return new ResponseEntity<>( ApiResponse.successEmpty( "Movimiento eliminado exitosamente", "MOVIMIENTO_DELETED"), HttpStatus.OK);
        }
}
