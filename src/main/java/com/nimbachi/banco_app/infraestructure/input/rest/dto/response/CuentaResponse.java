package com.nimbachi.banco_app.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.nimbachi.banco_app.domain.enums.TipoCuenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaResponse {
    private Long id;
    private String numeroCuenta;
    private TipoCuenta tipo;
    private BigDecimal saldoInicial;
    private boolean estado;
    private LocalDate fechaApertura;
    private Long clienteId;
    private List<MovimientoResponse> movimientos;
}