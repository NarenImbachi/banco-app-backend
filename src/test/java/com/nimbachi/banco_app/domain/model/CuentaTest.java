package com.nimbachi.banco_app.domain.model;

import com.nimbachi.banco_app.domain.enums.TipoCuenta;
import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import com.nimbachi.banco_app.domain.exception.CupoExcedidoException;
import com.nimbachi.banco_app.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Cuenta — dominio")
class CuentaTest {

    @Test
    void crear_ok() {
        // arrange

        // act
        Cuenta cuenta = Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("1000"), 1L);

        // assert
        assertThat(cuenta.getNumeroCuenta()).isEqualTo("CTA001");
        assertThat(cuenta.getSaldoDisponible()).isEqualTo(new BigDecimal("1000"));
        assertThat(cuenta.isEstado()).isTrue();
    }

    @Test
    void crear_numeroCuentaNull_lanzaExcepcion() {
        // arrange

        // act + assert
        assertThatThrownBy(() -> Cuenta.crear(null, TipoCuenta.AHORRO, new BigDecimal("1000"), 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void crear_saldoNegativo_lanzaExcepcion() {
        // arrange

        // act + assert
        assertThatThrownBy(() -> Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("-1"), 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void actualizarDatos_ok() {
        // arrange
        Cuenta cuenta = Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("1000"), 1L);

        // act
        cuenta.actualizarDatos(TipoCuenta.CORRIENTE, false);

        // assert
        assertThat(cuenta.getTipo()).isEqualTo(TipoCuenta.CORRIENTE);
        assertThat(cuenta.isEstado()).isFalse();
    }

    @Test
    void procesarMovimiento_deposito_ok() {
        // arrange
        Cuenta cuenta = Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("1000"), 1L);
        Movimiento mov = new Movimiento();
        mov.setTipo(TipoMovimiento.DEPOSITO);
        mov.setValor(new BigDecimal("500"));

        // act
        cuenta.procesarMovimiento(mov, List.of());

        // assert
        assertThat(cuenta.getSaldoDisponible()).isEqualTo(new BigDecimal("1500"));
    }

    @Test
    void procesarMovimiento_depositoNegativo_lanzaExcepcion() {
        // arrange
        Cuenta cuenta = Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("1000"), 1L);
        Movimiento mov = new Movimiento();
        mov.setTipo(TipoMovimiento.DEPOSITO);
        mov.setValor(new BigDecimal("-10"));

        // act + assert
        assertThatThrownBy(() -> cuenta.procesarMovimiento(mov, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void procesarMovimiento_retiro_ok() {
        // arrange
        Cuenta cuenta = Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("1000"), 1L);
        Movimiento mov = new Movimiento();
        mov.setTipo(TipoMovimiento.RETIRO);
        mov.setValor(new BigDecimal("-200"));

        // act
        cuenta.procesarMovimiento(mov, List.of());

        // assert
        assertThat(cuenta.getSaldoDisponible()).isEqualTo(new BigDecimal("800"));
    }

    @Test
    void procesarMovimiento_saldoInsuficiente() {
        // arrange
        Cuenta cuenta = Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("100"), 1L);
        Movimiento mov = new Movimiento();
        mov.setTipo(TipoMovimiento.RETIRO);
        mov.setValor(new BigDecimal("-200"));

        // act + assert
        assertThatThrownBy(() -> cuenta.procesarMovimiento(mov, List.of()))
                .isInstanceOf(SaldoInsuficienteException.class);
    }

    @Test
    void procesarMovimiento_cupoExcedido() {
        // arrange
        Cuenta cuenta = Cuenta.crear("CTA001", TipoCuenta.AHORRO, new BigDecimal("2000"), 1L);

        Movimiento retiroPrevio = new Movimiento();
        retiroPrevio.setTipo(TipoMovimiento.RETIRO);
        retiroPrevio.setValor(new BigDecimal("-900"));

        Movimiento nuevo = new Movimiento();
        nuevo.setTipo(TipoMovimiento.RETIRO);
        nuevo.setValor(new BigDecimal("-200"));

        // act + assert
        assertThatThrownBy(() -> cuenta.procesarMovimiento(nuevo, List.of(retiroPrevio)))
                .isInstanceOf(CupoExcedidoException.class);
    }
}