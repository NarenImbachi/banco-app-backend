package com.nimbachi.banco_app.application.service;

import com.nimbachi.banco_app.application.output.IClientePersistencePort;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IClienteRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Pruebas unitarias para {@link ClienteService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService — pruebas unitarias")
class ClienteServiceTest {

    @Mock
    private IClientePersistencePort clientePersistencePort;

    @Mock
    private IClienteRestMapper clienteRestMapper;

    @InjectMocks
    private ClienteService clienteService;

    // fixtures

    private Cliente clienteValido;
    private ClienteResponse clienteResponseValido;

    @BeforeEach
    void setUp() {
        clienteValido = buildCliente(1L, "CLI001", "Ana Torres",
                "F", 30, "1234567890", "Calle 10 #5-20", "3001234567");

        clienteResponseValido = ClienteResponse.builder()
                .id(1L)
                .clienteId("CLI001")
                .nombre("Ana Torres")
                .genero("F")
                .edad(30)
                .identificacion("1234567890")
                .direccion("Calle 10 #5-20")
                .telefono("3001234567")
                .estado(true)
                .build();
    }

    @Nested
    @DisplayName("crearCliente()")
    class CrearCliente {

        @Test
        @DisplayName("debe crear y retornar el cliente cuando los datos son válidos")
        void crearCliente_datosValidos_retornaClienteResponse() {

            // given
            given(clientePersistencePort.existsByClienteId("CLI001")).willReturn(false);
            given(clientePersistencePort.save(any(Cliente.class))).willReturn(clienteValido);
            given(clienteRestMapper.domainToResponse(clienteValido)).willReturn(clienteResponseValido);

            // when
            ClienteResponse resultado = clienteService.crearCliente(clienteValido);

            // then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getClienteId()).isEqualTo("CLI001");
            assertThat(resultado.getNombre()).isEqualTo("Ana Torres");
            assertThat(resultado.isEstado()).isTrue();

            then(clientePersistencePort).should().existsByClienteId("CLI001");
            then(clientePersistencePort).should().save(argThat(c -> c.getEstado() == Boolean.TRUE));
            then(clienteRestMapper).should().domainToResponse(clienteValido);
        }

        @Test
        @DisplayName("debe lanzar RuntimeException cuando el clienteId ya existe")
        void crearCliente_clienteIdDuplicado_lanzaExcepcion() {

            // given
            given(clientePersistencePort.existsByClienteId("CLI001")).willReturn(true);

            // when / then
            assertThatThrownBy(() -> clienteService.crearCliente(clienteValido))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("CLI001")
                    .hasMessageContaining("ya existe");

            then(clientePersistencePort).should(never()).save(any());
            then(clienteRestMapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("debe asignar estado=true automáticamente al crear")
        void crearCliente_estadoAutoAsignado_estadoEsTrue() {

            // given — cliente sin estado definido explícitamente
            Cliente sinEstado = buildCliente(null, "CLI002", "Pedro Ruiz",
                    "M", 25, "9876543210", "Av. 5 #1-10", "3109876543");
            sinEstado.setEstado(null);

            Cliente guardado = buildCliente(2L, "CLI002", "Pedro Ruiz",
                    "M", 25, "9876543210", "Av. 5 #1-10", "3109876543");
            guardado.setEstado(true);

            given(clientePersistencePort.existsByClienteId("CLI002")).willReturn(false);
            given(clientePersistencePort.save(any(Cliente.class))).willReturn(guardado);
            given(clienteRestMapper.domainToResponse(guardado)).willReturn(
                    ClienteResponse.builder().estado(true).build());

            // when
            ClienteResponse resultado = clienteService.crearCliente(sinEstado);

            // then
            assertThat(resultado.isEstado()).isTrue();
            then(clientePersistencePort).should().save(argThat(c -> Boolean.TRUE.equals(c.getEstado())));
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar los campos permitidos y retornar el response")
        void actualizar_datosValidos_retornaClienteActualizado() {

            // given
            Cliente actualizado = buildCliente(1L, "CLI001", "Ana Torres Gómez",
                    "F", 31, "1234567890", "Calle 20 #8-15", "3007654321");
            ClienteResponse responseActualizado = ClienteResponse.builder()
                    .id(1L).nombre("Ana Torres Gómez").edad(31).build();

            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));
            given(clientePersistencePort.save(any(Cliente.class))).willReturn(actualizado);
            given(clienteRestMapper.domainToResponse(actualizado)).willReturn(responseActualizado);

            // when
            ClienteResponse resultado = clienteService.actualizar(1L, actualizado);

            // then
            assertThat(resultado.getNombre()).isEqualTo("Ana Torres Gómez");
            assertThat(resultado.getEdad()).isEqualTo(31);
            then(clientePersistencePort).should().findById(1L);
            then(clientePersistencePort).should().save(any(Cliente.class));
        }

        @Test
        @DisplayName("debe lanzar excepción cuando el cliente no existe")
        void actualizar_clienteNoExiste_lanzaExcepcion() {

            // given
            given(clientePersistencePort.findById(99L)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> clienteService.actualizar(99L, clienteValido))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99")
                    .hasMessageContaining("no encontrado");

            then(clientePersistencePort).should(never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepción al intentar cambiar la identificación")
        void actualizar_cambioIdentificacion_lanzaExcepcion() {

            // given
            Cliente conIdentificacionDiferente = buildCliente(1L, "CLI001", "Ana Torres",
                    "F", 30, "DIFERENTE_ID", "Calle 10 #5-20", "3001234567");

            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));

            // when / then
            assertThatThrownBy(() -> clienteService.actualizar(1L, conIdentificacionDiferente))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("identificación");

            then(clientePersistencePort).should(never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepción al intentar cambiar el clienteId")
        void actualizar_cambioClienteId_lanzaExcepcion() {

            // given
            Cliente conClienteIdDiferente = buildCliente(1L, "CLI_OTRO", "Ana Torres",
                    "F", 30, "1234567890", "Calle 10 #5-20", "3001234567");

            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));

            // when / then
            assertThatThrownBy(() -> clienteService.actualizar(1L, conClienteIdDiferente))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("clienteId");

            then(clientePersistencePort).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("debe delegar la eliminación al puerto de persistencia")
        void eliminar_idValido_delegaAlPuerto() {

            // given
            willDoNothing().given(clientePersistencePort).deleteById(1L);

            // when
            clienteService.eliminar(1L);

            // then
            then(clientePersistencePort).should().deleteById(1L);
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el Optional con el cliente cuando existe")
        void obtenerPorId_clienteExiste_retornaOptional() {

            // given
            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));

            // when
            Optional<Cliente> resultado = clienteService.obtenerPorId(1L);

            // then
            assertThat(resultado).isPresent();
            assertThat(resultado.get().getClienteId()).isEqualTo("CLI001");
        }

        @Test
        @DisplayName("debe retornar Optional vacío cuando el cliente no existe")
        void obtenerPorId_clienteNoExiste_retornaOptionalVacio() {

            // given
            given(clientePersistencePort.findById(404L)).willReturn(Optional.empty());

            // when
            Optional<Cliente> resultado = clienteService.obtenerPorId(404L);

            // then
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("listarTodos()")
    class ListarTodos {

        @Test
        @DisplayName("debe retornar la lista mapeada de todos los clientes")
        void listarTodos_hayClientes_retornaListaResponse() {

            // given
            List<Cliente> clientes = List.of(clienteValido);
            List<ClienteResponse> responses = List.of(clienteResponseValido);

            given(clientePersistencePort.findAll()).willReturn(clientes);
            given(clienteRestMapper.domainListToResponseList(clientes)).willReturn(responses);

            // when
            List<ClienteResponse> resultado = clienteService.listarTodos();

            // then
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getClienteId()).isEqualTo("CLI001");
        }

        @Test
        @DisplayName("debe retornar lista vacía cuando no hay clientes registrados")
        void listarTodos_sinClientes_retornaListaVacia() {

            // given
            given(clientePersistencePort.findAll()).willReturn(List.of());
            given(clienteRestMapper.domainListToResponseList(List.of())).willReturn(List.of());

            // when
            List<ClienteResponse> resultado = clienteService.listarTodos();

            // then
            assertThat(resultado).isEmpty();
        }
    }

    // helpers
    private static Cliente buildCliente(Long id, String clienteId, String nombre,
            String genero, Integer edad, String identificacion,
            String direccion, String telefono) {
        Cliente c = new Cliente();
        c.setId(id);
        c.setClienteId(clienteId);
        c.setNombre(nombre);
        c.setGenero(genero);
        c.setEdad(edad);
        c.setIdentificacion(identificacion);
        c.setDireccion(direccion);
        c.setTelefono(telefono);
        c.setEstado(true);
        return c;
    }
}