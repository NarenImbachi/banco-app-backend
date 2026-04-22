package com.nimbachi.banco_app.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMovimientoResponse {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private String cliente;

    private String numeroCuenta;

    private String tipoCuenta;

    private BigDecimal saldoInicial;

    private Boolean estado;

    private BigDecimal movimiento;

    private BigDecimal saldoDisponible;

}
