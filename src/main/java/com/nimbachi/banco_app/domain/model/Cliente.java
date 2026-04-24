package com.nimbachi.banco_app.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Persona {
    private String clienteId;
    private String contrasena;
    private boolean estado;
    private List<Cuenta> cuentas;

    public static Cliente crearCliente(String clienteId, String contrasena, String nombre, String genero, int edad,
            String identificacion, String direccion, String telefono) {

        if (clienteId == null || clienteId.trim().isEmpty())
            throw new IllegalArgumentException("El clienteId no puede ser nulo o vacío");

        if (contrasena == null || contrasena.trim().isEmpty())
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");

        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setContrasena(contrasena);
        cliente.setNombre(nombre);
        cliente.setGenero(genero);
        cliente.setEdad(edad);
        cliente.setIdentificacion(identificacion);
        cliente.setDireccion(direccion);
        cliente.setTelefono(telefono);
        cliente.setEstado(true);
        return cliente;
    }

    public void actualizarDatos(String nombre, String genero, int edad, String direccion, String telefono,
            boolean estado) {
        this.setNombre(nombre);
        this.setGenero(genero);
        this.setEdad(edad);
        this.setDireccion(direccion);
        this.setTelefono(telefono);
        this.setEstado(estado);
    }
}