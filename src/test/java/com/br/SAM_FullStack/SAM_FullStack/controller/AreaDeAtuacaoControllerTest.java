package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.CustomUserDetailsService; // Import concrete class
import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService; // Import service
import com.br.SAM_FullStack.SAM_FullStack.config.SecurityConfig;
import com.br.SAM_FullStack.SAM_FullStack.config.JwtAuthenticationFilter;
import com.br.SAM_FullStack.SAM_FullStack.config.GlobalExceptionHandler;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.service.AreaDeAtuacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = {AreaDeAtuacaoController.class, GlobalExceptionHandler.class})
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("Testes de Integração Web do AreaDeAtuacaoController")
class AreaDeAtuacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AreaDeAtuacaoService areaDeAtuacaoService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private AreaDeAtuacao area;

    @BeforeEach
    void setUp() {
        area = new AreaDeAtuacao();
        area.setId(1L);
        area.setNome("Tecnologia");
    }

    @Test
    @DisplayName("Deve retornar lista de áreas e status OK (Autenticado)")
    @WithMockUser
    void findAll_quandoAutenticado_deveRetornarListaEStatusOK() throws Exception {
        when(areaDeAtuacaoService.findAll()).thenReturn(Arrays.asList(area));
        mockMvc.perform(get("/areas/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Tecnologia"));
    }

    @Test
    @DisplayName("Deve retornar lista de áreas e status OK (Não Autenticado)")
    void findAll_quandoNaoAutenticado_deveRetornarOK() throws Exception {
        when(areaDeAtuacaoService.findAll()).thenReturn(Arrays.asList(area));
        mockMvc.perform(get("/areas/findAll"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve salvar área e retornar status CREATED")
    @WithMockUser
    void save_quandoAutenticadoEValido_deveRetornarCreatedEArea() throws Exception {
        when(areaDeAtuacaoService.save(any(AreaDeAtuacao.class))).thenReturn(area);
        mockMvc.perform(post("/areas/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve atualizar área e retornar status OK")
    @WithMockUser
    void update_quandoAutenticadoEValido_deveRetornarOKeArea() throws Exception {
        when(areaDeAtuacaoService.update(eq(1L), any(AreaDeAtuacao.class))).thenReturn(area);
        mockMvc.perform(put("/areas/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve deletar área e retornar status NO_CONTENT")
    @WithMockUser
    void delete_quandoAutenticadoEIdExistente_deveRetornarNoContent() throws Exception {
        doNothing().when(areaDeAtuacaoService).delete(1L);
        mockMvc.perform(delete("/areas/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve buscar por início do nome e retornar lista e status OK")
    @WithMockUser
    void getAreaPorInicioDoNome_quandoAutenticadoEEncontrado_deveRetornarListaEStatusOK() throws Exception {
        when(areaDeAtuacaoService.buscarPorInicioDoNome("Tec")).thenReturn(Arrays.asList(area));
        mockMvc.perform(get("/areas/buscar-por-inicio").param("prefixo", "Tec"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Tecnologia"));
    }

    @Test
    @DisplayName("Deve buscar por início do nome e retornar NO_CONTENT")
    @WithMockUser
    void getAreaPorInicioDoNome_quandoAutenticadoENaoEncontrado_deveRetornarNoContent() throws Exception {
        when(areaDeAtuacaoService.buscarPorInicioDoNome("X")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/areas/buscar-por-inicio").param("prefixo", "X"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar área do aluno logado e status OK")
    @WithMockUser
    void getAreaDeAtuacaoDoAlunoLogado_quandoAutenticadoEEncontrado_deveRetornarAreaEStatusOK() throws Exception {
        when(areaDeAtuacaoService.findByAlunoLogado()).thenReturn(area);
        mockMvc.perform(get("/areas/por-aluno-logado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tecnologia"));
    }

    @Test
    @DisplayName("Deve retornar NOT_FOUND quando área do aluno logado não encontrada")
    @WithMockUser
    void getAreaDeAtuacaoDoAlunoLogado_quandoAutenticadoENaoEncontrado_deveRetornarNotFound() throws Exception {
        when(areaDeAtuacaoService.findByAlunoLogado()).thenReturn(null);
        mockMvc.perform(get("/areas/por-aluno-logado"))
                .andExpect(status().isNotFound());
    }
}