package com.nimbachi.banco_app.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCuentaResponse {

    private String clienteNombre;
    private String clienteIdentificacion;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;
    private List<CuentaDetail> cuentas = new ArrayList<>();
    private BigDecimal totalDepositosConsolidado;
    private BigDecimal totalRetirosConsolidado;
    private BigDecimal saldoFinalConsolidado;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuentaDetail {
        private String numeroCuenta;
        private String tipo;
        private BigDecimal saldoActual;
        private List<MovimientoDetail> movimientos = new ArrayList<>();
        private BigDecimal totalDepositos;
        private BigDecimal totalRetiros;
        private BigDecimal saldoFinal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimientoDetail {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate fecha;
        private String tipo;
        private BigDecimal valor;
        private BigDecimal saldo;
    }
}
