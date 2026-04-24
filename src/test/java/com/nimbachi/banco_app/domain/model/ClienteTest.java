package com.nimbachi.banco_app.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Cliente — dominio")
class ClienteTest {

    @Test
    void crearCliente_ok() {
        // arrange
        String clienteId = "CLI001";

        // act
        Cliente cliente = Cliente.crearCliente(
                clienteId,
                "password",
                "Ana Torres",
                "F",
                30,
                "1234567890",
                "Calle 10",
                "3001234567");

        // assert
        assertThat(cliente).isNotNull();
        assertThat(cliente.getClienteId()).isEqualTo(clienteId);
        assertThat(cliente.isEstado()).isTrue();
        assertThat(cliente.getNombre()).isEqualTo("Ana Torres");
    }

    @Test
    void crearCliente_clienteIdNull_lanzaExcepcion() {
        // arrange

        // act + assert
        assertThatThrownBy(() -> Cliente.crearCliente(
                null,
                "password",
                "Ana",
                "F",
                30,
                "123",
                "dir",
                "tel")).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("clienteId");
    }

    @Test
    void crearCliente_passwordVacia_lanzaExcepcion() {
        // arrange

        // act + assert
        assertThatThrownBy(() -> Cliente.crearCliente(
                "CLI001",
                " ",
                "Ana",
                "F",
                30,
                "123",
                "dir",
                "tel")).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("contraseña");
    }

    @Test
    void actualizarDatos_ok() {
        // arrange
        Cliente cliente = Cliente.crearCliente(
                "CLI001",
                "password",
                "Ana",
                "F",
                30,
                "123",
                "dir",
                "tel");

        // act
        cliente.actualizarDatos(
                "Ana Gomez",
                "F",
                31,
                "nueva dir",
                "999",
                false);

        // assert
        assertThat(cliente.getNombre()).isEqualTo("Ana Gomez");
        assertThat(cliente.getEdad()).isEqualTo(31);
        assertThat(cliente.isEstado()).isFalse();
    }
}