package com.nimbachi.banco_app.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.nimbachi.banco_app.domain.enums.TipoCuenta;
import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import com.nimbachi.banco_app.domain.exception.CupoExcedidoException;
import com.nimbachi.banco_app.domain.exception.SaldoInsuficienteException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {
    private Long id;
    private String numeroCuenta;
    private TipoCuenta tipo;
    private BigDecimal saldoInicial;
    private BigDecimal saldoDisponible;
    private boolean estado; // false = inactivo, true = activo
    private Long clienteId; 
    private List<Movimiento> movimientos = new ArrayList<>();

    private static final BigDecimal LIMITE_RETIRO_DIARIO = new BigDecimal("1000");

    public static Cuenta crear(String numeroCuenta, TipoCuenta tipo, BigDecimal saldoInicial, Long clienteId) {
        if (numeroCuenta == null || numeroCuenta.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser nulo o vacío.");
        }
        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser nulo o negativo.");
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(numeroCuenta);
        cuenta.setTipo(tipo);
        cuenta.setSaldoInicial(saldoInicial);
        cuenta.setSaldoDisponible(saldoInicial);
        cuenta.setClienteId(clienteId);
        cuenta.setEstado(true); 
        return cuenta;
    }

    public void actualizarDatos(TipoCuenta tipo, boolean estado) {
        this.setTipo(tipo);
        this.setEstado(estado);
    }

    /**
     * Método central para procesar cualquier movimiento en la cuenta.
     * Encapsula toda la lógica de negocio de créditos y débitos.
     * @param movimiento El movimiento a procesar (ya debe tener el valor con el signo correcto).
     * @param retirosDelDia La lista de retiros realizados en el día de hoy para esta cuenta.
     */
    public void procesarMovimiento(Movimiento movimiento, List<Movimiento> retirosDelDia) {
        if (movimiento.getTipo() == TipoMovimiento.RETIRO) {
            
            if (this.saldoDisponible.add(movimiento.getValor()).compareTo(BigDecimal.ZERO) < 0) 
                throw new SaldoInsuficienteException("Saldo no disponible");
            
            validarCupoDiario(movimiento, retirosDelDia);
        } else { 
            if (movimiento.getValor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El valor del depósito debe ser positivo.");
            }
        }

        this.saldoDisponible = this.saldoDisponible.add(movimiento.getValor());
    }
    
    private void validarCupoDiario(Movimiento nuevoRetiro, List<Movimiento> retirosDelDia) {
        BigDecimal totalRetiradoHoy = retirosDelDia.stream()
                .map(Movimiento::getValor) 
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalConNuevoRetiro = totalRetiradoHoy.add(nuevoRetiro.getValor());

        if (totalConNuevoRetiro.abs().compareTo(LIMITE_RETIRO_DIARIO) > 0) {
            throw new CupoExcedidoException("Cupo diario Excedido");
        }
    }
}
