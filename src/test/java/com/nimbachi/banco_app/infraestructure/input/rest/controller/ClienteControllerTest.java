package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbachi.banco_app.application.input.IClienteCommandUseCase;
import com.nimbachi.banco_app.application.input.IClienteQueryUseCase;
import com.nimbachi.banco_app.domain.model.Cliente;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.UpdateClienteRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.ClienteResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IClienteRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@DisplayName("ClienteController — pruebas de capa web")
class ClienteControllerTest {

        private static final String BASE_URL = "/api/clientes";

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private IClienteCommandUseCase clienteCommandUseCase;

        @MockitoBean
        private IClienteQueryUseCase clienteQueryUseCase;

        @MockitoBean
        private IClienteRestMapper clienteRestMapper;

        private ClienteResponse responseBase;
        private CreateClienteRequest createRequest;
        private UpdateClienteRequest updateRequest;

        @BeforeEach
        void setUp() {
                responseBase = ClienteResponse.builder()
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

                createRequest = CreateClienteRequest.builder()
                                .clienteId("CLI001")
                                .nombre("Ana Torres")
                                .genero("F")
                                .edad(30)
                                .identificacion("1234567890")
                                .direccion("Calle 10 #5-20")
                                .telefono("3001234567")
                                .contrasena("segura123")
                                .build();

                updateRequest = UpdateClienteRequest.builder()
                                .nombre("Ana Torres Gómez")
                                .direccion("Calle 20 #8-15")
                                .telefono("3007654321")
                                .estado(true)
                                .build();
        }

        @Nested
        class PostCliente {

                @Test
                void post_ok() throws Exception {

                        given(clienteRestMapper.requestToDomain(any()))
                                        .willReturn(new Cliente());

                        given(clienteCommandUseCase.crearCliente(any()))
                                        .willReturn(responseBase);

                        mockMvc.perform(post(BASE_URL)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(createRequest)))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.data.clienteId", is("CLI001")));
                }
        }

        @Nested
        class GetTodos {

                @Test
                void get_all_ok() throws Exception {

                        given(clienteQueryUseCase.listarTodos())
                                        .willReturn(List.of(responseBase));

                        mockMvc.perform(get(BASE_URL))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.data", hasSize(1)));
                }
        }

        // ================= GET BY ID =================
        @Nested
        class GetPorId {

                @Test
                void get_by_id_ok() throws Exception {

                        Cliente dominio = new Cliente();
                        dominio.setId(1L); // 🔥 IMPORTANTE

                        given(clienteQueryUseCase.obtenerPorId(1L))
                                        .willReturn(Optional.of(dominio));

                        given(clienteRestMapper.domainToResponse(dominio))
                                        .willReturn(responseBase);

                        mockMvc.perform(get(BASE_URL + "/1"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.data.id", is(1)));
                }

                @Test
                void get_by_id_not_found() throws Exception {

                        given(clienteQueryUseCase.obtenerPorId(99L))
                                        .willReturn(Optional.empty());

                        mockMvc.perform(get(BASE_URL + "/99"))
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.message", is("Cliente no encontrado"))); // 👈 ajustado
                }
        }

        @Nested
        class PutCliente {

                @Test
                void put_ok() throws Exception {

                        Cliente dominio = new Cliente();

                        given(clienteQueryUseCase.obtenerPorId(1L))
                                        .willReturn(Optional.of(dominio)); // 🔥 CLAVE

                        given(clienteRestMapper.updateRequestToDomain(any()))
                                        .willReturn(new Cliente());

                        given(clienteCommandUseCase.actualizar(eq(1L), any()))
                                        .willReturn(responseBase);

                        mockMvc.perform(put(BASE_URL + "/1")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                        .andExpect(status().isOk());
                }

                @Test
                void put_not_found() throws Exception {

                        given(clienteQueryUseCase.obtenerPorId(99L))
                                        .willReturn(Optional.empty()); // 🔥 CLAVE

                        mockMvc.perform(put(BASE_URL + "/99")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest)))
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        class DeleteCliente {

                @Test
                void delete_ok() throws Exception {

                        given(clienteQueryUseCase.obtenerPorId(1L))
                                        .willReturn(Optional.of(new Cliente())); // 🔥 CLAVE

                        willDoNothing().given(clienteCommandUseCase).eliminar(1L);

                        mockMvc.perform(delete(BASE_URL + "/1"))
                                        .andExpect(status().isOk());
                }

                @Test
                void delete_not_found() throws Exception {

                        given(clienteQueryUseCase.obtenerPorId(99L))
                                        .willReturn(Optional.empty()); // 🔥 CLAVE

                        mockMvc.perform(delete(BASE_URL + "/99"))
                                        .andExpect(status().isNotFound());
                }
        }
}