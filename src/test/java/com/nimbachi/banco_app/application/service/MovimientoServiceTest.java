package com.nimbachi.banco_app.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.application.output.IMovimientoPersistencePort;
import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IMovimientoRestMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private ICuentaPersistencePort cuentaPersistencePort;

    @Mock
    private IMovimientoPersistencePort movimientoPersistencePort;

    @Mock
    private IMovimientoRestMapper movimientoRestMapper;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setSaldoDisponible(new BigDecimal("1000"));
        cuenta.setMovimientos(List.of());
    }

    @Test
    void registrarMovimiento_deposito_ok() {

        Movimiento movimiento = Movimiento.builder()
                .tipo(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("200"))
                .cuentaId(1L)
                .build();

        when(cuentaPersistencePort.findById(1L))
                .thenReturn(Optional.of(cuenta));

        when(movimientoPersistencePort.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(movimientoRestMapper.domainToResponse(any()))
                .thenReturn(new MovimientoResponse());

        MovimientoResponse response =
                movimientoService.registrarMovimiento(1L, movimiento);

        assertNotNull(response);
        verify(cuentaPersistencePort).save(any());
    }

    @Test
    void registrarMovimiento_retiro_ok() {

        Movimiento movimiento = Movimiento.builder()
                .tipo(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("-200"))
                .cuentaId(1L)
                .fecha(LocalDate.now())
                .build();

        when(cuentaPersistencePort.findById(1L))
                .thenReturn(Optional.of(cuenta));

        when(movimientoPersistencePort.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(movimientoRestMapper.domainToResponse(any()))
                .thenReturn(new MovimientoResponse());

        MovimientoResponse response =
                movimientoService.registrarMovimiento(1L, movimiento);

        assertNotNull(response);
    }

    @Test
    void listarMovimientos_ok() {

        when(movimientoPersistencePort.obtenerListadoMovimientos())
                .thenReturn(List.of(new MovimientoListadoResponse()));

        List<MovimientoListadoResponse> result =
                movimientoService.listarMovimientosFormateados();

        assertFalse(result.isEmpty());
    }

    @Test
    void eliminar_ok() {

        movimientoService.eliminar(1L);

        verify(movimientoPersistencePort).delete(1L);
    }

    @Test
    void registrarMovimiento_cuentaNoExiste_lanzaError() {

        Movimiento movimiento = Movimiento.builder()
                .tipo(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("100"))
                .cuentaId(1L)
                .build();

        when(cuentaPersistencePort.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                movimientoService.registrarMovimiento(1L, movimiento)
        );
    }

    @Test
    void registrarMovimiento_depositoNegativo_lanzaError() {

        Movimiento movimiento = Movimiento.builder()
                .tipo(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("-100"))
                .cuentaId(1L)
                .build();

        when(cuentaPersistencePort.findById(1L))
                .thenReturn(Optional.of(cuenta));

        assertThrows(RuntimeException.class, () ->
                movimientoService.registrarMovimiento(1L, movimiento)
        );
    }

    @Test
    void registrarMovimiento_retiroPositivo_lanzaError() {

        Movimiento movimiento = Movimiento.builder()
                .tipo(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("100")) // ❌ mal signo
                .cuentaId(1L)
                .build();

        when(cuentaPersistencePort.findById(1L))
                .thenReturn(Optional.of(cuenta));

        assertThrows(RuntimeException.class, () ->
                movimientoService.registrarMovimiento(1L, movimiento)
        );
    }

    @Test
    void registrarMovimiento_sinSaldo_lanzaError() {

        cuenta.setSaldoDisponible(BigDecimal.ZERO);

        Movimiento movimiento = Movimiento.builder()
                .tipo(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("-100"))
                .cuentaId(1L)
                .build();

        when(cuentaPersistencePort.findById(1L))
                .thenReturn(Optional.of(cuenta));

        assertThrows(RuntimeException.class, () ->
                movimientoService.registrarMovimiento(1L, movimiento)
        );
    }

    @Test
    void registrarMovimiento_excedeCupoDiario_lanzaError() {

        Movimiento retiroHoy = Movimiento.builder()
                .tipo(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("-900"))
                .fecha(LocalDate.now())
                .build();

        cuenta.setMovimientos(List.of(retiroHoy));

        Movimiento nuevoRetiro = Movimiento.builder()
                .tipo(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("-200"))
                .cuentaId(1L)
                .fecha(LocalDate.now())
                .build();

        when(cuentaPersistencePort.findById(1L))
                .thenReturn(Optional.of(cuenta));

        assertThrows(RuntimeException.class, () ->
                movimientoService.registrarMovimiento(1L, nuevoRetiro)
        );
    }
}