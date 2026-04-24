package com.nimbachi.banco_app.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nimbachi.banco_app.domain.enums.TipoCuenta;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MovimientoListadoResponse {

    private Long id;

    private Long cuentaId;

    private LocalDate fecha;

    private String numeroCuenta;

    private String tipoCuenta;

    private Double saldoInicial;

    private Boolean estado;

    private String movimiento;

    public MovimientoListadoResponse(
            Long id,
            Long cuentaId,
            LocalDate fecha,
            String numeroCuenta,
            TipoCuenta tipoCuenta,
            BigDecimal saldoInicial,
            Boolean estado,
            String movimiento) {

        this.id = id;
        this.cuentaId = cuentaId;
        this.fecha = fecha;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta.name();
        this.saldoInicial = saldoInicial.doubleValue();
        this.estado = estado;
        this.movimiento = movimiento;
    }
}