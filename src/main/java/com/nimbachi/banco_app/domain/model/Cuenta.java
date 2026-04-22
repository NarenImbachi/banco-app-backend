package com.nimbachi.banco_app.domain.model;

import java.math.BigDecimal;
import java.util.List;

import com.nimbachi.banco_app.domain.enums.TipoCuenta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {
    private Long id;
    private String numeroCuenta;
    private TipoCuenta tipo;
    private BigDecimal saldoInicial;
    private boolean estado; // false = inactivo, true = activo
    private Long clienteId; // FK a Cliente
    private List<Movimiento> movimientos;
}
