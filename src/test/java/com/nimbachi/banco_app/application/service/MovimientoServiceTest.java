package com.nimbachi.banco_app.application.service;

import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.application.output.IMovimientoPersistencePort;
import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IMovimientoRestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
                cuenta = new Cuenta(1L, "12345", null,
                                new BigDecimal("1000"), new BigDecimal("1000"),
                                true, 1L, new ArrayList<>());
        }

        @Test
        void registrarMovimiento_deposito_ok() {

                Movimiento input = Movimiento.builder()
                                .tipo(TipoMovimiento.DEPOSITO)
                                .valor(new BigDecimal("200"))
                                .build();

                given(cuentaPersistencePort.findById(1L))
                                .willReturn(Optional.of(cuenta));

                given(movimientoPersistencePort.findRetirosByCuentaIdAndFecha(eq(1L), any()))
                                .willReturn(List.of());

                given(movimientoPersistencePort.save(any()))
                                .willAnswer(inv -> inv.getArgument(0));

                given(movimientoRestMapper.domainToResponse(any()))
                                .willReturn(new MovimientoResponse());

                MovimientoResponse response = movimientoService.registrarMovimiento(1L, input);

                assertThat(response).isNotNull();

                then(movimientoPersistencePort).should()
                                .save(argThat(m -> m.getValor().compareTo(new BigDecimal("200")) == 0 &&
                                                m.getSaldo().compareTo(new BigDecimal("1200")) == 0));

                then(cuentaPersistencePort).should().save(cuenta);
        }

        @Test
        void registrarMovimiento_retiro_ok() {

                Movimiento input = Movimiento.builder()
                                .tipo(TipoMovimiento.RETIRO)
                                .valor(new BigDecimal("-200"))
                                .build();

                given(cuentaPersistencePort.findById(1L))
                                .willReturn(Optional.of(cuenta));

                given(movimientoPersistencePort.findRetirosByCuentaIdAndFecha(eq(1L), any()))
                                .willReturn(List.of());

                given(movimientoPersistencePort.save(any()))
                                .willAnswer(inv -> inv.getArgument(0));

                given(movimientoRestMapper.domainToResponse(any()))
                                .willReturn(new MovimientoResponse());

                MovimientoResponse response = movimientoService.registrarMovimiento(1L, input);

                assertThat(response).isNotNull();

                then(movimientoPersistencePort).should()
                                .save(argThat(m -> m.getSaldo().compareTo(new BigDecimal("800")) == 0));
        }

        @Test
        void registrarMovimiento_cuentaNoExiste_lanzaError() {

                Movimiento input = Movimiento.builder()
                                .tipo(TipoMovimiento.DEPOSITO)
                                .valor(new BigDecimal("100"))
                                .build();

                given(cuentaPersistencePort.findById(1L))
                                .willReturn(Optional.empty());

                assertThatThrownBy(() -> movimientoService.registrarMovimiento(1L, input))
                                .isInstanceOf(RuntimeException.class)
                                .hasMessageContaining("no existe");
        }

        @Test
        void registrarMovimiento_depositoNegativo_lanzaError() {

                Movimiento input = Movimiento.builder()
                                .tipo(TipoMovimiento.DEPOSITO)
                                .valor(new BigDecimal("-100"))
                                .build();

                given(cuentaPersistencePort.findById(1L))
                                .willReturn(Optional.of(cuenta));

                given(movimientoPersistencePort.findRetirosByCuentaIdAndFecha(eq(1L), any()))
                                .willReturn(List.of());

                assertThatThrownBy(() -> movimientoService.registrarMovimiento(1L, input))
                                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void registrarMovimiento_retiroPositivo_lanzaError() {

                Movimiento input = Movimiento.builder()
                                .tipo(TipoMovimiento.RETIRO)
                                .valor(new BigDecimal("100"))
                                .build();

                given(cuentaPersistencePort.findById(1L))
                                .willReturn(Optional.of(cuenta));

                given(movimientoPersistencePort.findRetirosByCuentaIdAndFecha(eq(1L), any()))
                                .willReturn(List.of());

                assertThatThrownBy(() -> movimientoService.registrarMovimiento(1L, input))
                                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void registrarMovimiento_sinSaldo_lanzaError() {

                cuenta.setSaldoDisponible(BigDecimal.ZERO);

                Movimiento input = Movimiento.builder()
                                .tipo(TipoMovimiento.RETIRO)
                                .valor(new BigDecimal("-100"))
                                .build();

                given(cuentaPersistencePort.findById(1L))
                                .willReturn(Optional.of(cuenta));

                given(movimientoPersistencePort.findRetirosByCuentaIdAndFecha(eq(1L), any()))
                                .willReturn(List.of());

                assertThatThrownBy(() -> movimientoService.registrarMovimiento(1L, input))
                                .isInstanceOf(RuntimeException.class);
        }

        @Test
        void registrarMovimiento_excedeCupoDiario_lanzaError() {

                Movimiento retiroHoy = Movimiento.builder()
                                .tipo(TipoMovimiento.RETIRO)
                                .valor(new BigDecimal("-900"))
                                .fecha(LocalDate.now())
                                .build();

                Movimiento nuevo = Movimiento.builder()
                                .tipo(TipoMovimiento.RETIRO)
                                .valor(new BigDecimal("-200"))
                                .build();

                given(cuentaPersistencePort.findById(1L))
                                .willReturn(Optional.of(cuenta));

                given(movimientoPersistencePort.findRetirosByCuentaIdAndFecha(eq(1L), any()))
                                .willReturn(List.of(retiroHoy));

                assertThatThrownBy(() -> movimientoService.registrarMovimiento(1L, nuevo))
                                .isInstanceOf(RuntimeException.class);
        }

        @Test
        void listarMovimientos_ok() {

                given(movimientoPersistencePort.obtenerListadoMovimientos())
                                .willReturn(List.of(new MovimientoListadoResponse()));

                List<MovimientoListadoResponse> result = movimientoService.listarMovimientosFormateados();

                assertThat(result).isNotEmpty();
        }

        @Test
        void eliminar_ok() {

                movimientoService.eliminar(1L);

                then(movimientoPersistencePort).should().delete(1L);
        }

        @Test
        void obtenerPorId() {

                Movimiento mov = Movimiento.builder().build();

                given(movimientoPersistencePort.findById(1L))
                                .willReturn(Optional.of(mov));

                Optional<Movimiento> result = movimientoService.obtenerPorId(1L);

                assertThat(result).isPresent();
        }
}