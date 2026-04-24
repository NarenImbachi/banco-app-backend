package com.nimbachi.banco_app.domain.model;

import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Movimiento — dominio")
class MovimientoTest {

    private Cuenta cuenta() {
        Cuenta c = new Cuenta();
        c.setId(1L);
        return c;
    }

    @Test
    void crear_deposito_ok() {
        // arrange
        Cuenta cuenta = cuenta();

        // act
        Movimiento movimiento = Movimiento.crear(cuenta, TipoMovimiento.DEPOSITO, new BigDecimal("100"));

        // assert
        assertThat(movimiento.getCuentaId()).isEqualTo(1L);
        assertThat(movimiento.getTipo()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(movimiento.getValor()).isEqualTo(new BigDecimal("100"));
        assertThat(movimiento.getFecha()).isNotNull();
    }

    @Test
    void crear_deposito_invalido() {
        // arrange
        Cuenta cuenta = cuenta();

        // act & assert
        assertThatThrownBy(() -> Movimiento.crear(cuenta, TipoMovimiento.DEPOSITO, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void crear_retiro_ok() {
        // arrange
        Cuenta cuenta = cuenta();

        // act
        Movimiento movimiento = Movimiento.crear(cuenta, TipoMovimiento.RETIRO, new BigDecimal("-50"));

        // assert
        assertThat(movimiento.getTipo()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(movimiento.getValor()).isEqualTo(new BigDecimal("-50"));
    }

    @Test
    void crear_retiro_invalido() {
        // arrange
        Cuenta cuenta = cuenta();

        // act & assert
        assertThatThrownBy(() -> Movimiento.crear(cuenta, TipoMovimiento.RETIRO, new BigDecimal("50")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}