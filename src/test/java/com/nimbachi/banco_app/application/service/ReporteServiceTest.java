package com.nimbachi.banco_app.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.application.output.IClientePersistencePort;
import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.application.output.IMovimientoPersistencePort;
import com.nimbachi.banco_app.domain.enums.TipoCuenta;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ReporteMovimientoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private IClientePersistencePort clientePersistencePort;

    @Mock
    private ICuentaPersistencePort cuentaPersistencePort;

    @Mock
    private IMovimientoPersistencePort movimientoPersistencePort;

    @InjectMocks
    private ReporteService reporteService;

    private Cliente cliente;
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan");

        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setNumeroCuenta("12345");
        cuenta.setTipo(TipoCuenta.AHORRO);
        cuenta.setSaldoInicial(new BigDecimal("1000"));
        cuenta.setEstado(true);
    }

    @Test
    void generarReporte_conMovimientos_ok() {

        LocalDate inicio = LocalDate.now().minusDays(5);
        LocalDate fin = LocalDate.now();

        Movimiento mov1 = Movimiento.builder()
                .fecha(inicio.plusDays(1))
                .valor(new BigDecimal("200"))
                .saldo(new BigDecimal("1200"))
                .build();

        Movimiento mov2 = Movimiento.builder()
                .fecha(inicio.plusDays(2))
                .valor(new BigDecimal("-100"))
                .saldo(new BigDecimal("1100"))
                .build();

        when(clientePersistencePort.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(cuentaPersistencePort.findByClienteId(1L))
                .thenReturn(List.of(cuenta));

        when(movimientoPersistencePort.findByCuentaIdAndFechaBetween(any(), any(), any()))
                .thenReturn(List.of(mov1, mov2));

        List<ReporteMovimientoResponse> result =
                reporteService.generarReporteEstadoCuenta(1L, inicio, fin);

        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getCliente());
        assertEquals("12345", result.get(0).getNumeroCuenta());
    }

    @Test
    void generarReporte_sinMovimientos_ok() {

        LocalDate inicio = LocalDate.now().minusDays(5);
        LocalDate fin = LocalDate.now();

        when(clientePersistencePort.findById(1L))
                .thenReturn(Optional.of(cliente));

        when(cuentaPersistencePort.findByClienteId(1L))
                .thenReturn(List.of(cuenta));

        when(movimientoPersistencePort.findByCuentaIdAndFechaBetween(any(), any(), any()))
                .thenReturn(List.of());

        List<ReporteMovimientoResponse> result =
                reporteService.generarReporteEstadoCuenta(1L, inicio, fin);

        assertTrue(result.isEmpty());
    }

    @Test
    void generarReporte_clienteNoExiste_lanzaError() {

        when(clientePersistencePort.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                reporteService.generarReporteEstadoCuenta(
                        1L,
                        LocalDate.now().minusDays(1),
                        LocalDate.now())
        );
    }

    @Test
    void generarReporte_fechaInvalida_lanzaError() {

        when(clientePersistencePort.findById(1L))
                .thenReturn(Optional.of(cliente));

        assertThrows(RuntimeException.class, () ->
                reporteService.generarReporteEstadoCuenta(
                        1L,
                        LocalDate.now(),
                        LocalDate.now().minusDays(1))
        );
    }

    @Test
    void generarReporte_rangoMayor90Dias_lanzaError() {

        when(clientePersistencePort.findById(1L))
                .thenReturn(Optional.of(cliente));

        assertThrows(RuntimeException.class, () ->
                reporteService.generarReporteEstadoCuenta(
                        1L,
                        LocalDate.now().minusDays(100),
                        LocalDate.now())
        );
    }
}