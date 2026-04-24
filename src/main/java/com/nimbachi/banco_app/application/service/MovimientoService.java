package com.nimbachi.banco_app.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nimbachi.banco_app.application.input.IMovimientoCommandUseCase;
import com.nimbachi.banco_app.application.input.IMovimientoQueryUseCase;
import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.application.output.IMovimientoPersistencePort;
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
    //

    @Override
    public Optional<Movimiento> obtenerPorId(Long id) {
        log.debug("Obteniendo movimiento ID: {}", id);
        return movimientoPersistencePort.findById(id);
    }

    @Override
    @Transactional
    public MovimientoResponse registrarMovimiento(Long cuentaId, Movimiento movimiento) {
        log.info("Iniciando registro de movimiento para cuenta ID: {}", cuentaId);

        Cuenta cuenta = cuentaPersistencePort.findById(cuentaId)
            .orElseThrow(() -> new RuntimeException("La cuenta con ID " + cuentaId + " no existe"));
        
        List<Movimiento> retirosDelDia = movimientoPersistencePort.findRetirosByCuentaIdAndFecha(
            cuentaId, 
            LocalDate.now()
        );

        Movimiento nuevoMovimiento = Movimiento.crear(
            cuenta, 
            movimiento.getTipo(), 
            movimiento.getValor()
        );
        
        // La cuenta ahora es responsable de aplicar las reglas de negocio.
        cuenta.procesarMovimiento(nuevoMovimiento, retirosDelDia);

        // 3. PERSISTIR ESTADO: Guardar los resultados en la base de datos.
        // Asignamos el saldo resultante al movimiento antes de guardarlo.
        nuevoMovimiento.setSaldo(cuenta.getSaldoDisponible());
        
        Movimiento movimientoGuardado = movimientoPersistencePort.save(nuevoMovimiento);
        cuentaPersistencePort.save(cuenta);

        log.info("Movimiento registrado. Nuevo saldo de cuenta {}: {}", cuenta.getNumeroCuenta(), cuenta.getSaldoDisponible());
        return movimientoRestMapper.domainToResponse(movimientoGuardado);
    }

    @Override
    public void eliminar(Long id) {
        log.warn("Intento de eliminar movimiento ID: {}", id);
        // throw new RuntimeException("Los movimientos no se pueden eliminar. Se
        // preservan para auditoría");
        movimientoPersistencePort.delete(id);
    }

    @Override
    public List<MovimientoListadoResponse> listarMovimientosFormateados() {
        log.debug("Obteniendo listado de movimientos formateados");
        return movimientoPersistencePort.obtenerListadoMovimientos();

    }
}
