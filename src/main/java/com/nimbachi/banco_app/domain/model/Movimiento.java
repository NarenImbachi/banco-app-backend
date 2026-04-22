package com.nimbachi.banco_app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nimbachi.banco_app.domain.enums.TipoMovimiento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimiento {
    private Long id;
    private LocalDate fecha;
    private TipoMovimiento tipo;
    private BigDecimal valor;
    private BigDecimal saldo;

    private Long cuentaId; // FK a Cuenta
}
