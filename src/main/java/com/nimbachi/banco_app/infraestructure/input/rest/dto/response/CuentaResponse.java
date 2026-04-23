package com.nimbachi.banco_app.infraestructure.input.rest.dto.response;

import java.math.BigDecimal;
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
    private BigDecimal saldoDisponible;
    private boolean estado;
    private String estadoTexto;
    private Long clienteId;
    private List<MovimientoResponse> movimientos;
}