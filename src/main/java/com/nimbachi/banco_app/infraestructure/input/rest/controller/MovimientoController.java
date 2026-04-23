package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nimbachi.banco_app.appication.input.IMovimientoCommandUseCase;
import com.nimbachi.banco_app.appication.input.IMovimientoQueryUseCase;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateMovimientoRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ApiResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IMovimientoRestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Slf4j
public class MovimientoController {

        private final IMovimientoCommandUseCase movimientoCommandUseCase;
        private final IMovimientoQueryUseCase movimientoQueryUseCase;
        private final IMovimientoRestMapper movimientoRestMapper;

        @PostMapping
        public ResponseEntity<ApiResponse<MovimientoResponse>> registrarMovimiento(
                        @Valid @RequestBody CreateMovimientoRequest request) {
                log.info("POST /api/movimientos - Registrando movimiento en cuenta: {}", request.getCuentaId());

                Movimiento movimiento = new Movimiento();
                movimiento = movimientoRestMapper.requestToDomain(request);
                /*movimiento.setTipo(request.getTipo());
                movimiento.setValor(request.getValor());
                movimiento.setCuentaId(request.getCuentaId());*/

                MovimientoResponse movimientoRegistrado = movimientoCommandUseCase.registrarMovimiento(movimiento.getCuentaId(), movimiento);
                return new ResponseEntity<>(
                                ApiResponse.success(movimientoRegistrado, "Movimiento registrado exitosamente"),
                                HttpStatus.CREATED);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Object>> eliminar(@PathVariable Long id) {
                log.info("DELETE /api/movimientos/{} - Intentando eliminar movimiento", id);

                Optional<Movimiento> movimientoOptional = movimientoQueryUseCase.obtenerPorId(id);
                if (movimientoOptional.isEmpty()) {
                        return new ResponseEntity<>(
                                        ApiResponse.error("Movimiento no encontrado", "MOVIMIENTO_NOT_FOUND",
                                                        HttpStatus.NOT_FOUND, "/api/movimientos/" + id),
                                        HttpStatus.NOT_FOUND);
                }

                movimientoCommandUseCase.eliminar(id);
                return new ResponseEntity<>(
                                ApiResponse.successEmpty("Movimiento eliminado exitosamente", "MOVIMIENTO_DELETED"),
                                HttpStatus.OK);
        }

        @GetMapping("/listado")
        public ResponseEntity<ApiResponse<List<MovimientoListadoResponse>>> listarMovimientosFormateados() {
                log.info("GET /api/movimientos/listado");
                List<MovimientoListadoResponse> movimientos = movimientoQueryUseCase.listarMovimientosFormateados();
                return ResponseEntity.ok(ApiResponse.success(movimientos, "Movimientos listados correctamente"));
        }
}
