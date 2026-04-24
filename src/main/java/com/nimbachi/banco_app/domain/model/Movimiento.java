package com.nimbachi.banco_app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nimbachi.banco_app.domain.enums.TipoMovimiento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Movimiento {
    private Long id;
    private LocalDate fecha;
    private TipoMovimiento tipo;
    private BigDecimal valor; // + para crédito, - para débito
    private BigDecimal saldo; // Saldo de la cuenta DESPUÉS de este movimiento
    private Long cuentaId;

    public static Movimiento crear(Cuenta cuenta, TipoMovimiento tipo, BigDecimal valorConSigno) {

        if (tipo == TipoMovimiento.DEPOSITO && valorConSigno.compareTo(BigDecimal.ZERO) <= 0) 
            throw new IllegalArgumentException("El depósito debe ser un valor positivo.");
        
        if (tipo == TipoMovimiento.RETIRO && valorConSigno.compareTo(BigDecimal.ZERO) >= 0) 
            throw new IllegalArgumentException("El retiro debe ser un valor negativo.");

        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaId(cuenta.getId());
        movimiento.setFecha(LocalDate.now());
        movimiento.setTipo(tipo);
        movimiento.setValor(valorConSigno);
        
        return movimiento;
    }
}
