package com.nimbachi.banco_app.appication.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbachi.banco_app.appication.input.IReporteQueryUseCase;
import com.nimbachi.banco_app.appication.output.IClientePersistencePort;
import com.nimbachi.banco_app.appication.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.appication.output.IMovimientoPersistencePort;
import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.EstadoCuentaResponse;

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
    public EstadoCuentaResponse generarReporteEstadoCuenta(Long clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Generando reporte estado de cuenta para cliente ID: {} entre {} y {}",
                clienteId, fechaInicio, fechaFin);
        
        Optional<Cliente> clienteOptional = clientePersistencePort.findById(clienteId);
        if (clienteOptional.isEmpty()) 
            throw new RuntimeException("El cliente con ID " + clienteId + " no existe");
        
        Cliente cliente = clienteOptional.get();

        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        long diasDiferencia = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        if (diasDiferencia > DIAS_MAXIMOS) {
            throw new RuntimeException("El rango de fechas no puede ser mayor a " + DIAS_MAXIMOS + " días");
        }

        // Obtener todas las cuentas del cliente
        List<Cuenta> cuentas = cuentaPersistencePort.findByClienteId(clienteId);

        // Construir respuesta
        EstadoCuentaResponse reporte = new EstadoCuentaResponse();
        reporte.setClienteNombre(cliente.getNombre());
        reporte.setClienteIdentificacion(cliente.getIdentificacion());
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);

        BigDecimal totalDepositosConsolidado = BigDecimal.ZERO;
        BigDecimal totalRetirosConsolidado = BigDecimal.ZERO;
        BigDecimal saldoFinalConsolidado = BigDecimal.ZERO;

        for (Cuenta cuenta : cuentas) {
            EstadoCuentaResponse.CuentaDetail cuentaDetail = new EstadoCuentaResponse.CuentaDetail();
            cuentaDetail.setNumeroCuenta(cuenta.getNumeroCuenta());
            cuentaDetail.setTipo(cuenta.getTipo().toString());
            cuentaDetail.setSaldoActual(cuenta.getSaldoInicial());

            // Obtener movimientos en rango de fechas
            List<Movimiento> movimientos = movimientoPersistencePort
                    .findByCuentaIdAndFechaBetween(cuenta.getId(), fechaInicio, fechaFin);

            // Convertir movimientos a DTO
            List<EstadoCuentaResponse.MovimientoDetail> movimientosDetail = movimientos.stream()
                    .map(m -> new EstadoCuentaResponse.MovimientoDetail(
                            m.getFecha(),
                            m.getTipo().toString(),
                            m.getValor(),
                            m.getSaldo()
                    ))
                    .toList();

            cuentaDetail.setMovimientos(movimientosDetail);

            // Calcular totales por cuenta
            BigDecimal totalDepositos = calcularTotalDepositos(movimientos);
            BigDecimal totalRetiros = calcularTotalRetiros(movimientos);

            cuentaDetail.setTotalDepositos(totalDepositos);
            cuentaDetail.setTotalRetiros(totalRetiros);
            cuentaDetail.setSaldoFinal(cuenta.getSaldoInicial());

            reporte.getCuentas().add(cuentaDetail);

            // Acumular en totales consolidados
            totalDepositosConsolidado = totalDepositosConsolidado.add(totalDepositos);
            totalRetirosConsolidado = totalRetirosConsolidado.add(totalRetiros);
            saldoFinalConsolidado = saldoFinalConsolidado.add(cuenta.getSaldoInicial());
        }

        reporte.setTotalDepositosConsolidado(totalDepositosConsolidado);
        reporte.setTotalRetirosConsolidado(totalRetirosConsolidado);
        reporte.setSaldoFinalConsolidado(saldoFinalConsolidado);

        log.info("Reporte generado exitosamente");
        return reporte;
    }

    /**
     * Calcula total de depósitos
     */
    private BigDecimal calcularTotalDepositos(List<Movimiento> movimientos) {
        return movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.DEPOSITO)
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula total de retiros
     */
    private BigDecimal calcularTotalRetiros(List<Movimiento> movimientos) {
        return movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.RETIRO)
                .map(Movimiento::getValor)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
