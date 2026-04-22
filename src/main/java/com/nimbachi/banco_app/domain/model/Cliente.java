package com.nimbachi.banco_app.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Persona {
    private String clienteId;
    private String contraseña;
    private Boolean estado;
    private List<Cuenta> cuentas;
}