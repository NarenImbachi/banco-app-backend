package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nimbachi.banco_app.appication.input.IReporteQueryUseCase;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ApiResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ReporteMovimientoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

    private final IReporteQueryUseCase reporteQueryUseCase;

    @GetMapping("/estado-cuenta")
    public ResponseEntity<ApiResponse<List<ReporteMovimientoResponse>>> generarReporteEstadoCuenta(
            @RequestParam Long clienteId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        
        List<ReporteMovimientoResponse> reporte = reporteQueryUseCase.generarReporteEstadoCuenta(clienteId, fechaInicio, fechaFin);
        
        return new ResponseEntity<>(ApiResponse.success(reporte, "Reporte generado exitosamente"), HttpStatus.OK);
    }
}