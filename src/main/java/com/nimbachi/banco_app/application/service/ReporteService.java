package com.nimbachi.banco_app.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbachi.banco_app.application.input.IReporteQueryUseCase;
import com.nimbachi.banco_app.application.output.IClientePersistencePort;
import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.application.output.IMovimientoPersistencePort;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ReporteMovimientoResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService implements IReporteQueryUseCase {

    private final IClientePersistencePort clientePersistencePort;
    private final ICuentaPersistencePort cuentaPersistencePort;
    private final IMovimientoPersistencePort movimientoPersistencePort;

    private static final long DIAS_MAXIMOS = 90;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteMovimientoResponse> generarReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.info("Generando reporte para cliente ID: {}", clienteId);

        Cliente cliente = clientePersistencePort.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no existe"));

        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("Fecha inicio mayor que fecha fin");
        }

        long dias = ChronoUnit.DAYS.between(fechaInicio, fechaFin);

        if (dias > DIAS_MAXIMOS) {
            throw new RuntimeException(
                    "Rango mayor a " + DIAS_MAXIMOS + " días");
        }

        List<Cuenta> cuentas = cuentaPersistencePort.findByClienteId(clienteId);

        List<ReporteMovimientoResponse> reporte = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {

            List<Movimiento> movimientos = new ArrayList<>(
                    movimientoPersistencePort
                            .findByCuentaIdAndFechaBetween(cuenta.getId(), fechaInicio, fechaFin));

            movimientos.sort(Comparator.comparing(Movimiento::getFecha));
            BigDecimal saldoInicialCuenta;

            if (!movimientos.isEmpty()) {
                Movimiento primerMovimiento = movimientos.get(0);
                saldoInicialCuenta = primerMovimiento
                        .getSaldo()
                        .subtract(primerMovimiento.getValor());
            } else {
                saldoInicialCuenta = cuenta.getSaldoInicial();
            }

            for (Movimiento mov : movimientos) {

                ReporteMovimientoResponse fila = new ReporteMovimientoResponse();
                fila.setFecha(mov.getFecha());
                fila.setCliente(cliente.getNombre());
                fila.setNumeroCuenta(cuenta.getNumeroCuenta());
                fila.setTipoCuenta(cuenta.getTipo().toString());
                fila.setSaldoInicial(saldoInicialCuenta);
                fila.setEstado(cuenta.isEstado());
                fila.setMovimiento(mov.getValor());
                fila.setSaldoDisponible(mov.getSaldo());
                reporte.add(fila);
            }
        }

        log.info("Reporte generado con {} filas",
                reporte.size());

        return reporte;
    }
}
