package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbachi.banco_app.application.input.ICuentaCommandUseCase;
import com.nimbachi.banco_app.application.input.ICuentaQueryUseCase;
import com.nimbachi.banco_app.domain.enums.TipoCuenta;
import com.nimbachi.banco_app.domain.model.Cuenta;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateCuentaRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.UpdateCuentaRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.CuentaResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.ICuentaRestMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
@DisplayName("CuentaController — pruebas de capa web")
class CuentaControllerTest {

    private static final String BASE_URL = "/api/cuentas";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICuentaCommandUseCase cuentaCommandUseCase;

    @MockitoBean
    private ICuentaQueryUseCase cuentaQueryUseCase;

    @MockitoBean
    private ICuentaRestMapper cuentaRestMapper;

    private CuentaResponse responseBase;
    private CreateCuentaRequest createRequest;
    private UpdateCuentaRequest updateRequest;

    @BeforeEach
    void setUp() {

        responseBase = CuentaResponse.builder()
                .id(1L)
                .numeroCuenta("12345")
                .tipo(TipoCuenta.AHORRO)
                .saldoInicial(BigDecimal.valueOf(1000))
                .saldoDisponible(BigDecimal.valueOf(1000))
                .estado(true)
                .clienteId(1L)
                .build();

        createRequest = CreateCuentaRequest.builder()
                .numeroCuenta("12345")
                .tipo(TipoCuenta.AHORRO)
                .saldoInicial(BigDecimal.valueOf(1000))
                .estado(true)
                .clienteId(1L)
                .build();

        updateRequest = UpdateCuentaRequest.builder()
                .tipo(TipoCuenta.CORRIENTE)
                .estado(true)
                .build();
    }

    // ───────────────────────── POST ─────────────────────────

    @Nested
    class PostCuenta {

        @Test
        void post_ok_retorna201() throws Exception {

            given(cuentaRestMapper.requestToDomain(any()))
                    .willReturn(new Cuenta());

            given(cuentaCommandUseCase.crearCuenta(any()))
                    .willReturn(responseBase);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.numeroCuenta", is("12345")))
                    .andExpect(jsonPath("$.message", containsString("Cuenta creada")));
        }

        @Test
        void post_error_retorna400() throws Exception {

            given(cuentaRestMapper.requestToDomain(any()))
                    .willReturn(new Cuenta());

            given(cuentaCommandUseCase.crearCuenta(any()))
                    .willThrow(new RuntimeException("ya existe"));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ───────────────────────── GET ALL ─────────────────────────

    @Test
    void get_all_ok() throws Exception {

        given(cuentaQueryUseCase.listarTodas())
                .willReturn(List.of(responseBase));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    // ───────────────────────── GET BY ID ─────────────────────────

    @Test
    void get_by_id_ok() throws Exception {

        Cuenta cuenta = new Cuenta();

        given(cuentaQueryUseCase.obtenerPorId(1L))
                .willReturn(Optional.of(cuenta));

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    void get_by_id_not_found() throws Exception {

        given(cuentaQueryUseCase.obtenerPorId(99L))
                .willReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    // ───────────────────────── PUT ─────────────────────────

    @Test
    void put_ok() throws Exception {

        Cuenta cuenta = new Cuenta();
        cuenta.setId(1L);

        given(cuentaQueryUseCase.obtenerPorId(1L))
                .willReturn(Optional.of(cuenta));

        given(cuentaCommandUseCase.actualizar(eq(1L), any()))
                .willReturn(responseBase);

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numeroCuenta", is("12345")));
    }

    @Test
    void put_not_found() throws Exception {

        given(cuentaQueryUseCase.obtenerPorId(99L))
                .willReturn(Optional.empty());

        mockMvc.perform(put(BASE_URL + "/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    // ───────────────────────── DELETE ─────────────────────────

    @Test
    void delete_ok() throws Exception {

        Cuenta cuenta = new Cuenta();

        given(cuentaQueryUseCase.obtenerPorId(1L))
                .willReturn(Optional.of(cuenta));

        willDoNothing().given(cuentaCommandUseCase).eliminar(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isOk()); // 👈 IMPORTANTE
    }

    @Test
    void delete_not_found() throws Exception {

        given(cuentaQueryUseCase.obtenerPorId(99L))
                .willReturn(Optional.empty());

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }
}