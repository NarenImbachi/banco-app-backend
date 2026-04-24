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
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService — pruebas unitarias")
class ClienteServiceTest {

    @Mock
    private IClientePersistencePort clientePersistencePort;

    @Mock
    private IClienteRestMapper clienteRestMapper;

    @InjectMocks
    private ClienteService clienteService;

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
        @DisplayName("debe guardar y retornar el cliente cuando los datos son válidos")
        void crearCliente_datosValidos_retornaClienteResponse() {

                Cliente clienteACrear = Cliente.crearCliente(
                    "CLI001", "password", "Ana Torres", "F", 30,
                    "1234567890", "Calle 10 #5-20", "3001234567");

            Cliente clienteCreadoPorFabrica = Cliente.crearCliente(
                    "CLI001", "password", "Ana Torres", "F", 30,
                    "1234567890", "Calle 10 #5-20", "3001234567");
            clienteCreadoPorFabrica.setId(1L);

            given(clientePersistencePort.existsByClienteId("CLI001")).willReturn(false);
            given(clientePersistencePort.save(any(Cliente.class))).willReturn(clienteCreadoPorFabrica);
            given(clienteRestMapper.domainToResponse(any(Cliente.class))).willReturn(clienteResponseValido);

            ClienteResponse resultado = clienteService.crearCliente(clienteACrear);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getClienteId()).isEqualTo("CLI001");

            then(clientePersistencePort).should().existsByClienteId("CLI001");
            then(clientePersistencePort).should().save(argThat(c -> c.isEstado()));
            then(clienteRestMapper).should().domainToResponse(clienteCreadoPorFabrica);
        }

        @Test
        @DisplayName("debe lanzar RuntimeException cuando el clienteId ya existe")
        void crearCliente_clienteIdDuplicado_lanzaExcepcion() {

            given(clientePersistencePort.existsByClienteId("CLI001")).willReturn(true);

            assertThatThrownBy(() -> clienteService.crearCliente(clienteValido))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("ya existe");

            then(clientePersistencePort).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar los campos permitidos y retornar el response")
        void actualizar_datosValidos_retornaClienteActualizado() {

            Cliente actualizado = buildCliente(1L, "CLI001", "Ana Torres Gómez",
                    "F", 31, "1234567890", "Calle 20 #8-15", "3007654321");

            ClienteResponse responseActualizado = ClienteResponse.builder()
                    .id(1L)
                    .nombre("Ana Torres Gómez")
                    .edad(31)
                    .build();

            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));
            given(clientePersistencePort.save(any(Cliente.class))).willReturn(actualizado);
            given(clienteRestMapper.domainToResponse(actualizado)).willReturn(responseActualizado);

            ClienteResponse resultado = clienteService.actualizar(1L, actualizado);

            assertThat(resultado.getNombre()).isEqualTo("Ana Torres Gómez");
            assertThat(resultado.getEdad()).isEqualTo(31);

            then(clientePersistencePort).should().findById(1L);
            then(clientePersistencePort).should().save(any(Cliente.class));
        }

        @Test
        @DisplayName("debe lanzar excepción cuando el cliente no existe")
        void actualizar_clienteNoExiste_lanzaExcepcion() {

            given(clientePersistencePort.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.actualizar(99L, clienteValido))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("99")
                    .hasMessageContaining("no encontrado");

            then(clientePersistencePort).should(never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepción al intentar cambiar la identificación")
        void actualizar_cambioIdentificacion_lanzaExcepcion() {

            Cliente conIdentificacionDiferente = buildCliente(1L, "CLI001", "Ana Torres",
                    "F", 30, "DIFERENTE_ID", "Calle 10 #5-20", "3001234567");

            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));

            assertThatThrownBy(() -> clienteService.actualizar(1L, conIdentificacionDiferente))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("identificación");

            then(clientePersistencePort).should(never()).save(any());
        }

        @Test
        @DisplayName("debe lanzar excepción al intentar cambiar el clienteId")
        void actualizar_cambioClienteId_lanzaExcepcion() {

            Cliente conClienteIdDiferente = buildCliente(1L, "CLI_OTRO", "Ana Torres",
                    "F", 30, "1234567890", "Calle 10 #5-20", "3001234567");

            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));

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

            willDoNothing().given(clientePersistencePort).deleteById(1L);

            clienteService.eliminar(1L);

            then(clientePersistencePort).should().deleteById(1L);
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("debe retornar el Optional con el cliente cuando existe")
        void obtenerPorId_clienteExiste_retornaOptional() {

            given(clientePersistencePort.findById(1L)).willReturn(Optional.of(clienteValido));

            Optional<Cliente> resultado = clienteService.obtenerPorId(1L);

            assertThat(resultado).isPresent();
            assertThat(resultado.get().getClienteId()).isEqualTo("CLI001");
        }

        @Test
        @DisplayName("debe retornar Optional vacío cuando el cliente no existe")
        void obtenerPorId_clienteNoExiste_retornaOptionalVacio() {

            given(clientePersistencePort.findById(404L)).willReturn(Optional.empty());

            Optional<Cliente> resultado = clienteService.obtenerPorId(404L);

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("listarTodos()")
    class ListarTodos {

        @Test
        @DisplayName("debe retornar la lista mapeada de todos los clientes")
        void listarTodos_hayClientes_retornaListaResponse() {

            List<Cliente> clientes = List.of(clienteValido);
            List<ClienteResponse> responses = List.of(clienteResponseValido);

            given(clientePersistencePort.findAll()).willReturn(clientes);
            given(clienteRestMapper.domainListToResponseList(clientes)).willReturn(responses);

            List<ClienteResponse> resultado = clienteService.listarTodos();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getClienteId()).isEqualTo("CLI001");
        }

        @Test
        @DisplayName("debe retornar lista vacía cuando no hay clientes registrados")
        void listarTodos_sinClientes_retornaListaVacia() {

            given(clientePersistencePort.findAll()).willReturn(List.of());
            given(clienteRestMapper.domainListToResponseList(List.of())).willReturn(List.of());

            List<ClienteResponse> resultado = clienteService.listarTodos();

            assertThat(resultado).isEmpty();
        }
    }

    private static Cliente buildCliente(Long id, String clienteId, String nombre,
            String genero, Integer edad, String identificacion,
            String direccion, String telefono) {
        Cliente c = Cliente.crearCliente(clienteId, "password", nombre, genero, edad,
                identificacion, direccion, telefono);
        c.setId(id);
        return c;
    }
}