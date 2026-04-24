package com.nimbachi.banco_app.application.service;

import com.nimbachi.banco_app.application.output.IClientePersistencePort;
import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.domain.enums.TipoCuenta;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.ICuentaRestMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaService — pruebas unitarias")
class CuentaServiceTest {

    @Mock
    private IClientePersistencePort clientePersistencePort;

    @Mock
    private ICuentaPersistencePort cuentaPersistencePort;

    @Mock
    private ICuentaRestMapper cuentaRestMapper;

    @InjectMocks
    private CuentaService cuentaService;

    private Cuenta cuentaValida;
    private CuentaResponse responseValido;

    @BeforeEach
    void setUp() {
        cuentaValida = new Cuenta(1L, "CTA001", TipoCuenta.AHORRO,
                new BigDecimal("1000"), new BigDecimal("1000"),
                true, 1L, new ArrayList<>());

        responseValido = CuentaResponse.builder()
                .id(1L)
                .numeroCuenta("CTA001")
                .tipo(TipoCuenta.AHORRO)
                .saldoDisponible(new BigDecimal("1000"))
                .estado(true)
                .clienteId(1L)
                .build();
    }

    @Nested
    @DisplayName("crearCuenta()")
    class Crear {

        @Test
        void crear_ok() {
            Cuenta cuentaACrear = new Cuenta(null, "CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), null, false, 1L, null);

            Cuenta cuentaCreada = Cuenta.crear("CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 1L);
            cuentaCreada.setId(1L);

            given(clientePersistencePort.findById(1L))
                    .willReturn(Optional.of(new Cliente()));

            given(cuentaPersistencePort.existsByNumeroCuenta("CTA001"))
                    .willReturn(false);

            given(cuentaPersistencePort.save(any(Cuenta.class)))
                    .willReturn(cuentaCreada);

            given(cuentaRestMapper.domainToResponse(any(Cuenta.class)))
                    .willReturn(responseValido);

            CuentaResponse result = cuentaService.crearCuenta(cuentaACrear);

            assertThat(result).isNotNull();
            assertThat(result.getNumeroCuenta()).isEqualTo("CTA001");

            then(cuentaPersistencePort).should().save(any(Cuenta.class));
        }

        @Test
        void crear_clienteNoExiste() {
            given(clientePersistencePort.findById(1L))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.crearCuenta(cuentaValida))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no existe");
        }

        @Test
        void crear_numeroDuplicado() {
            given(clientePersistencePort.findById(1L))
                    .willReturn(Optional.of(new Cliente()));

            given(cuentaPersistencePort.existsByNumeroCuenta("CTA001"))
                    .willReturn(true);

            assertThatThrownBy(() -> cuentaService.crearCuenta(cuentaValida))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("ya existe");
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        void actualizar_ok() {
            Cuenta cuentaSpy = spy(cuentaValida);

            Cuenta datosNuevos = new Cuenta(null, "OTRA",
                    TipoCuenta.CORRIENTE, null, null,
                    false, 99L, null);

            given(cuentaPersistencePort.findById(1L))
                    .willReturn(Optional.of(cuentaSpy));

            given(cuentaPersistencePort.save(any(Cuenta.class)))
                    .willReturn(cuentaSpy);

            given(cuentaRestMapper.domainToResponse(any(Cuenta.class)))
                    .willReturn(responseValido);

            CuentaResponse result = cuentaService.actualizar(1L, datosNuevos);

            assertThat(result).isNotNull();

            then(cuentaSpy).should().actualizarDatos(TipoCuenta.CORRIENTE, false);
            then(cuentaPersistencePort).should().save(cuentaSpy);
        }

        @Test
        void actualizar_noExiste() {
            given(cuentaPersistencePort.findById(1L))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.actualizar(1L, cuentaValida))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no existe");
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        void eliminar_ok() {
            cuentaValida.setMovimientos(List.of());

            given(cuentaPersistencePort.findById(1L))
                    .willReturn(Optional.of(cuentaValida));

            willDoNothing().given(cuentaPersistencePort).delete(1L);

            cuentaService.eliminar(1L);

            then(cuentaPersistencePort).should().delete(1L);
        }

        @Test
        void eliminar_noExiste() {
            given(cuentaPersistencePort.findById(1L))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.eliminar(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no existe");
        }

        @Test
        void eliminar_conMovimientos() {
            cuentaValida.setMovimientos(List.of(new Movimiento()));

            given(cuentaPersistencePort.findById(1L))
                    .willReturn(Optional.of(cuentaValida));

            assertThatThrownBy(() -> cuentaService.eliminar(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("movimientos");
        }
    }

    @Test
    void obtenerPorId() {
        given(cuentaPersistencePort.findById(1L))
                .willReturn(Optional.of(cuentaValida));

        Optional<Cuenta> result = cuentaService.obtenerPorId(1L);

        assertThat(result).isPresent();
    }

    @Test
    void listarTodas() {
        given(cuentaPersistencePort.findAll())
                .willReturn(List.of(cuentaValida));

        given(cuentaRestMapper.domainToResponse(cuentaValida))
                .willReturn(responseValido);

        List<CuentaResponse> result = cuentaService.listarTodas();

        assertThat(result).hasSize(1);
    }
}