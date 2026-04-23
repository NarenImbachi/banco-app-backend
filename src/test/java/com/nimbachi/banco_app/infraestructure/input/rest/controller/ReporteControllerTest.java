package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nimbachi.banco_app.application.input.IReporteQueryUseCase;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ReporteMovimientoResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IReporteQueryUseCase reporteQueryUseCase;

    @Test
    void generarReporte_ok() throws Exception {

        ReporteMovimientoResponse fila = new ReporteMovimientoResponse(
                LocalDate.now(),
                "Juan",
                "12345",
                "AHORROS",
                new BigDecimal("1000"),
                true,
                new BigDecimal("200"),
                new BigDecimal("1200")
        );

        when(reporteQueryUseCase.generarReporteEstadoCuenta(any(), any(), any()))
                .thenReturn(List.of(fila));

        mockMvc.perform(get("/api/reportes/estado-cuenta")
                        .param("clienteId", "1")
                        .param("fechaInicio", LocalDate.now().minusDays(5).toString())
                        .param("fechaFin", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].cliente").value("Juan"))
                .andExpect(jsonPath("$.data[0].numeroCuenta").value("12345"));
    }

    @Test
    void generarReporte_error_retorna400() throws Exception {

        when(reporteQueryUseCase.generarReporteEstadoCuenta(any(), any(), any()))
                .thenThrow(new RuntimeException("Cliente no existe"));

        mockMvc.perform(get("/api/reportes/estado-cuenta")
                        .param("clienteId", "1")
                        .param("fechaInicio", LocalDate.now().minusDays(5).toString())
                        .param("fechaFin", LocalDate.now().toString()))
                .andExpect(status().isBadRequest());
    }
}