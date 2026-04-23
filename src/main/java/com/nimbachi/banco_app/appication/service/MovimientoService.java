package com.nimbachi.banco_app.appication.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nimbachi.banco_app.appication.input.IMovimientoCommandUseCase;
import com.nimbachi.banco_app.appication.input.IMovimientoQueryUseCase;
import com.nimbachi.banco_app.appication.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.appication.output.IMovimientoPersistencePort;
import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IMovimientoRestMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoService implements IMovimientoCommandUseCase, IMovimientoQueryUseCase {

    private final ICuentaPersistencePort cuentaPersistencePort;
    private final IMovimientoPersistencePort movimientoPersistencePort;
    private final IMovimientoRestMapper movimientoRestMapper;
    private static final BigDecimal LIMITE_RETIRO_DIARIO = new BigDecimal("1000");

    @Override
    public Optional<Movimiento> obtenerPorId(Long id) {
        log.debug("Obteniendo movimiento ID: {}", id);
        return movimientoPersistencePort.findById(id);
    }

    @Override
    @Transactional
    public MovimientoResponse registrarMovimiento(Long cuentaId, Movimiento movimiento) {
        log.info("Registrando movimiento: {} de ${} en cuenta ID: {}",
                movimiento.getTipo(), movimiento.getValor(), movimiento.getCuentaId());

        Optional<Cuenta> cuentaOptional = cuentaPersistencePort.findById(movimiento.getCuentaId());
        if (cuentaOptional.isEmpty())
            throw new RuntimeException("La cuenta con ID " + movimiento.getCuentaId() + " no existe");

        Cuenta cuenta = cuentaOptional.get();

        // REGLA: Validar que el valor sea correcto según el tipo
        validarSignoMovimiento(movimiento);

        // REGLA: Validar saldo disponible para retiros
        if (movimiento.getTipo() == TipoMovimiento.RETIRO) {

            BigDecimal saldoActual = cuenta.getSaldoDisponible();

            if (saldoActual.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Intento de retiro con saldo no disponible. Cuenta ID: {}", movimiento.getCuentaId());
                throw new RuntimeException("Saldo no disponible");
            }

            // REGLA 3: Validar cupo diario de retiros
            validarCupoDiarioRetiros(movimiento, cuenta);
        }

        BigDecimal saldoActual = cuenta.getSaldoDisponible();
        BigDecimal nuevoSaldo = saldoActual.add(movimiento.getValor());
        movimiento.setSaldo(nuevoSaldo);

        if (movimiento.getFecha() == null) {
            movimiento.setFecha(LocalDate.now());
        }

        Movimiento movimientoGuardado = movimientoPersistencePort.save(movimiento);

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaPersistencePort.save(cuenta);

        log.info("Movimiento registrado exitosamente. Nuevo saldo: ${}", nuevoSaldo);
        return movimientoRestMapper.domainToResponse(movimientoGuardado);
    }

    @Override
    public void eliminar(Long id) {
        log.warn("Intento de eliminar movimiento ID: {}", id);
        // throw new RuntimeException("Los movimientos no se pueden eliminar. Se
        // preservan para auditoría");
        movimientoPersistencePort.delete(id);
    }

    /**
     * Valida que el signo del valor sea correcto según el tipo de movimiento
     * DEPOSITO debe ser POSITIVO, RETIRO debe ser NEGATIVO
     * 
     * @param movimiento Movimiento a validar
     * @throws RuntimeException si el valor no cumple con la regla de signo según el
     *                          tipo de movimiento
     */
    private void validarSignoMovimiento(Movimiento movimiento) {
        if (movimiento.getTipo() == TipoMovimiento.DEPOSITO) {
            if (movimiento.getValor().compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("El depósito debe ser un valor positivo");

        } else if (movimiento.getTipo() == TipoMovimiento.RETIRO) {
            if (movimiento.getValor().compareTo(BigDecimal.ZERO) >= 0)
                throw new RuntimeException("El retiro debe ser un valor negativo");

        }
    }

    /**
     * Valida que no se exceda el límite diario de retiros ($1000)
     */
    private void validarCupoDiarioRetiros(Movimiento movimiento, Cuenta cuenta) {
        LocalDate hoy = LocalDate.now();

        // Obtener todos los retiros del día de hoy
        List<Movimiento> retirosDia = cuenta.getMovimientos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.RETIRO && m.getFecha().equals(hoy))
                .toList();

        // Calcular total de retiros del día
        BigDecimal totalRetirosDia = retirosDia.stream()
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs(); // Convertir a positivo para comparar

        // El nuevo retiro viene negativo, convertir a positivo
        BigDecimal nuevoRetiroPositivo = movimiento.getValor().abs();

        // Validar que no se exceda el límite
        if (totalRetirosDia.add(nuevoRetiroPositivo).compareTo(LIMITE_RETIRO_DIARIO) > 0) {
            log.warn("Intento de exceder cupo diario. Retiros hoy: ${}, Nuevo retiro: ${}",
                    totalRetirosDia, nuevoRetiroPositivo);
            throw new RuntimeException("Cupo diario Excedido");
        }
    }

    @Override
    public List<MovimientoListadoResponse> listarMovimientosFormateados() {
        log.debug("Obteniendo listado de movimientos formateados");
        return movimientoPersistencePort.obtenerListadoMovimientos();

    }
}
