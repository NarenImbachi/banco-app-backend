package com.nimbachi.banco_app.infraestructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbachi.banco_app.application.input.IMovimientoCommandUseCase;
import com.nimbachi.banco_app.application.input.IMovimientoQueryUseCase;
import com.nimbachi.banco_app.domain.enums.TipoMovimiento;
import com.nimbachi.banco_app.domain.model.Movimiento;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.request.CreateMovimientoRequest;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoListadoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.dto.response.MovimientoResponse;
import com.nimbachi.banco_app.infraestructure.input.rest.mapper.IMovimientoRestMapper;

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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovimientoController.class)
@DisplayName("MovimientoController — pruebas web")
class MovimientoControllerTest {

    private static final String BASE_URL = "/api/movimientos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IMovimientoCommandUseCase movimientoCommandUseCase;

    @MockitoBean
    private IMovimientoQueryUseCase movimientoQueryUseCase;

    @MockitoBean
    private IMovimientoRestMapper movimientoRestMapper;

    private CreateMovimientoRequest request;
    private MovimientoResponse response;

    @BeforeEach
    void setUp() {

        request = CreateMovimientoRequest.builder()
                .fecha(LocalDate.now())
                .tipo(TipoMovimiento.DEPOSITO)
                .valor(BigDecimal.valueOf(500))
                .cuentaId(1L)
                .build();

        response = new MovimientoResponse();
        response.setId(1L);
    }

    @Nested
    class PostMovimiento {

        @Test
        void post_ok() throws Exception {

            given(movimientoRestMapper.requestToDomain(any()))
                    .willReturn(new Movimiento());

            given(movimientoCommandUseCase.registrarMovimiento(eq(1L), any()))
                    .willReturn(response);

            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message", containsString("Movimiento registrado")));
        }

        @Test
        void post_error_retorna400() throws Exception {

            Movimiento movimientoMock = new Movimiento();
            movimientoMock.setCuentaId(1L);

            given(movimientoRestMapper.requestToDomain(any()))
                    .willReturn(movimientoMock);

            given(movimientoCommandUseCase.registrarMovimiento(eq(1L), any()))
                    .willThrow(new RuntimeException("Saldo no disponible"));

            mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void delete_ok() throws Exception {

        given(movimientoQueryUseCase.obtenerPorId(1L))
                .willReturn(Optional.of(new Movimiento()));

        willDoNothing().given(movimientoCommandUseCase).eliminar(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_not_found() throws Exception {

        given(movimientoQueryUseCase.obtenerPorId(99L))
                .willReturn(Optional.empty());

        mockMvc.perform(delete(BASE_URL + "/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_listado_ok() throws Exception {

        MovimientoListadoResponse listado = new MovimientoListadoResponse();
        listado.setCuentaId(1L);

        given(movimientoQueryUseCase.listarMovimientosFormateados())
                .willReturn(List.of(listado));

        mockMvc.perform(get(BASE_URL + "/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}