package com.nimbachi.banco_app.application.service;

import com.nimbachi.banco_app.application.output.IClientePersistencePort;
import com.nimbachi.banco_app.application.output.ICuentaPersistencePort;
import com.nimbachi.banco_app.domain.enums.TipoCuenta;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.ICuentaRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
        cuentaValida = buildCuenta(1L, "CTA001", TipoCuenta.AHORRO,
                new BigDecimal("1000"), 1L);

        responseValido = CuentaResponse.builder()
                .id(1L)
                .numeroCuenta("CTA001")
                .tipo(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000"))
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
            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(new Cliente()));
            given(cuentaPersistencePort.existsByNumeroCuenta("CTA001")).willReturn(false);
            given(cuentaPersistencePort.save(any())).willReturn(cuentaValida);
            given(cuentaRestMapper.domainToResponse(cuentaValida)).willReturn(responseValido);

            CuentaResponse result = cuentaService.crearCuenta(cuentaValida);

            assertThat(result).isNotNull();
            assertThat(result.getNumeroCuenta()).isEqualTo("CTA001");

            then(cuentaPersistencePort).should().save(any());
        }

        @Test
        void crear_clienteNoExiste() {
            given(clientePersistencePort.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.crearCuenta(cuentaValida))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no existe");

            then(cuentaPersistencePort).should(never()).save(any());
        }

        @Test
        void crear_numeroDuplicado() {
            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(new Cliente()));
            given(cuentaPersistencePort.existsByNumeroCuenta("CTA001")).willReturn(true);

            assertThatThrownBy(() -> cuentaService.crearCuenta(cuentaValida))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("ya existe");

            then(cuentaPersistencePort).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        void actualizar_ok() {
            Cuenta existente = buildCuenta(1L, "CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 1L);

            Cuenta nuevo = buildCuenta(1L, "CTA001", TipoCuenta.CORRIENTE,
                    new BigDecimal("1000"), 1L);

            given(cuentaPersistencePort.findById(1L)).willReturn(Optional.of(existente));
            given(cuentaPersistencePort.save(any())).willReturn(nuevo);
            given(cuentaRestMapper.domainToResponse(nuevo)).willReturn(responseValido);

            CuentaResponse result = cuentaService.actualizar(1L, nuevo);

            assertThat(result).isNotNull();
            then(cuentaPersistencePort).should().save(any());
        }

        @Test
        void actualizar_noExiste() {
            given(cuentaPersistencePort.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.actualizar(1L, cuentaValida))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no existe");
        }

        @Test
        void actualizar_cambiarNumeroCuenta() {
            Cuenta existente = buildCuenta(1L, "CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 1L);

            Cuenta nuevo = buildCuenta(1L, "OTRA", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 1L);

            given(cuentaPersistencePort.findById(1L)).willReturn(Optional.of(existente));

            assertThatThrownBy(() -> cuentaService.actualizar(1L, nuevo))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("número de cuenta");
        }

        @Test
        void actualizar_cambiarCliente() {
            Cuenta existente = buildCuenta(1L, "CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 1L);

            Cuenta nuevo = buildCuenta(1L, "CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 99L);

            given(cuentaPersistencePort.findById(1L)).willReturn(Optional.of(existente));

            assertThatThrownBy(() -> cuentaService.actualizar(1L, nuevo))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cliente");
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        void eliminar_ok() {
            Cuenta cuenta = buildCuenta(1L, "CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 1L);
            cuenta.setMovimientos(List.of());

            given(cuentaPersistencePort.findById(1L)).willReturn(Optional.of(cuenta));
            willDoNothing().given(cuentaPersistencePort).delete(1L);

            cuentaService.eliminar(1L);

            then(cuentaPersistencePort).should().delete(1L);
        }

        @Test
        void eliminar_noExiste() {
            given(cuentaPersistencePort.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> cuentaService.eliminar(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no existe");
        }

        @Test
        void eliminar_conMovimientos() {
            Cuenta cuenta = buildCuenta(1L, "CTA001", TipoCuenta.AHORRO,
                    new BigDecimal("1000"), 1L);
            cuenta.setMovimientos(List.of(new Movimiento())); // simula movimiento

            given(cuentaPersistencePort.findById(1L)).willReturn(Optional.of(cuenta));

            assertThatThrownBy(() -> cuentaService.eliminar(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("movimientos");
        }
    }

    @Test
    void obtenerPorId() {
        given(cuentaPersistencePort.findById(1L)).willReturn(Optional.of(cuentaValida));

        Optional<Cuenta> result = cuentaService.obtenerPorId(1L);

        assertThat(result).isPresent();
    }

    @Test
    void listarTodas() {
        given(cuentaPersistencePort.findAll()).willReturn(List.of(cuentaValida));
        given(cuentaRestMapper.domainToResponse(cuentaValida)).willReturn(responseValido);

        List<CuentaResponse> result = cuentaService.listarTodas();

        assertThat(result).hasSize(1);
    }

    private Cuenta buildCuenta(Long id, String numero, TipoCuenta tipo,
            BigDecimal saldo, Long clienteId) {
        Cuenta c = new Cuenta();
        c.setId(id);
        c.setNumeroCuenta(numero);
        c.setTipo(tipo);
        c.setSaldoInicial(saldo);
        c.setSaldoDisponible(saldo);
        c.setEstado(true);
        c.setClienteId(clienteId);
        return c;
    }
}
