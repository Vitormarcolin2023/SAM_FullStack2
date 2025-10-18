package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.CustomUserDetailsService;
import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.config.SecurityConfig;
import com.br.SAM_FullStack.SAM_FullStack.config.JwtAuthenticationFilter;
import com.br.SAM_FullStack.SAM_FullStack.config.GlobalExceptionHandler;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        AlunoController.class,
        GlobalExceptionHandler.class
})
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
@DisplayName("Testes de Integração Web do AlunoController")
class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlunoService alunoService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private Aluno aluno;
    private Curso mockCurso;

    private final String TOKEN_VALIDO = "token.valido.jwt";
    private final String TOKEN_ALUNO_NAO_ENCONTRADO = "token.aluno.nao.existe.jwt";
    private final String TOKEN_INVALIDO = "token.invalido.jwt";

    private final String EMAIL_VALIDO = "teste@email.com";
    private final String EMAIL_NAO_ENCONTRADO = "naoexiste@email.com";

    @BeforeEach
    void setUp() {
        mockCurso = new Curso();
        mockCurso.setId(1L);
        mockCurso.setNome("Engenharia de Software");

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");
        aluno.setEmail(EMAIL_VALIDO);
        aluno.setRa(12345);
        aluno.setCurso(mockCurso);

        when(tokenService.extractEmail(TOKEN_VALIDO)).thenReturn(EMAIL_VALIDO);
        when(customUserDetailsService.loadUserByUsername(EMAIL_VALIDO)).thenReturn(aluno);
        when(tokenService.validateToken(TOKEN_VALIDO)).thenReturn(true);
        when(alunoService.findByEmail(EMAIL_VALIDO)).thenReturn(aluno);

        when(tokenService.extractEmail(TOKEN_ALUNO_NAO_ENCONTRADO)).thenReturn(EMAIL_NAO_ENCONTRADO);
        when(customUserDetailsService.loadUserByUsername(EMAIL_NAO_ENCONTRADO))
                .thenReturn(new User(EMAIL_NAO_ENCONTRADO, "", List.of()));
        when(tokenService.validateToken(TOKEN_ALUNO_NAO_ENCONTRADO)).thenReturn(true);
        when(alunoService.findByEmail(EMAIL_NAO_ENCONTRADO)).thenReturn(null);

        when(tokenService.extractEmail(TOKEN_INVALIDO)).thenThrow(new RuntimeException("Token inválido"));
    }


    @Test
    @DisplayName("Deve retornar lista de alunos e status OK")
    @WithMockUser
    void findAll_quandoAutenticado_deveRetornarListaDeAlunosEStatusOK() throws Exception {
        when(alunoService.findAll()).thenReturn(Arrays.asList(aluno));
        mockMvc.perform(get("/alunos/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Aluno Teste"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao buscar ID inexistente")
    @WithMockUser
    void findById_quandoAutenticadoEIdNaoExistente_deveRetornarBadRequest() throws Exception {
        String msgErro = "Aluno não encontrado";
        when(alunoService.findById(99L)).thenThrow(new RuntimeException(msgErro));

        mockMvc.perform(get("/alunos/findById/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(msgErro));
    }

    @Test
    @DisplayName("Deve salvar aluno, retornar status CREATED e mensagem")
    @WithMockUser
    void save_quandoAutenticadoEValido_deveRetornarCreatedEMensagem() throws Exception {
        when(alunoService.save(any(Aluno.class))).thenReturn(aluno);
        mockMvc.perform(post("/alunos/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aluno)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Aluno cadastrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve atualizar aluno, retornar status OK e aluno atualizado")
    @WithMockUser
    void update_quandoAutenticadoEValido_deveRetornarOKeAluno() throws Exception {
        when(alunoService.update(eq(1L), any(Aluno.class))).thenReturn(aluno);
        mockMvc.perform(put("/alunos/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aluno)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve deletar aluno e retornar status NO_CONTENT")
    @WithMockUser
    void delete_quandoAutenticadoEIdExistente_deveRetornarNoContent() throws Exception {
        doNothing().when(alunoService).delete(1L);
        mockMvc.perform(delete("/alunos/delet/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar perfil do aluno e status OK")
    void getAlunoProfile_quandoTokenValido_deveRetornarAlunoEStatusOK() throws Exception {
        mockMvc.perform(get("/alunos/me")
                        .header("Authorization", "Bearer " + TOKEN_VALIDO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL_VALIDO));
    }

    @Test
    @DisplayName("Deve retornar 404 quando aluno do token não encontrado")
    void getAlunoProfile_quandoAlunoNaoEncontrado_deveRetornarNotFound() throws Exception {
        mockMvc.perform(get("/alunos/me")
                        .header("Authorization", "Bearer " + TOKEN_ALUNO_NAO_ENCONTRADO))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException quando token falha na extração (erro no filtro)")
    void getAlunoProfile_quandoTokenInvalido_deveLancarExcecao() {
        String msgErro = "Token inválido";
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            mockMvc.perform(get("/alunos/me")
                    .header("Authorization", "Bearer " + TOKEN_INVALIDO));
        });

        assertEquals(msgErro, thrown.getMessage());
    }


    @Test
    @DisplayName("Deve retornar 403 Forbidden quando header ausente")
    void getAlunoProfile_quandoHeaderAusente_deveRetornarForbidden() throws Exception {
        mockMvc.perform(get("/alunos/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao buscar todos sem autenticação")
    void findAll_quandoNaoAutenticado_deveRetornarForbidden() throws Exception {
        mockMvc.perform(get("/alunos/findAll"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve buscar por nome, retornar lista e status OK")
    @WithMockUser
    void getAlunosPorNome_quandoAutenticadoEEncontrado_deveRetornarListaEStatusOK() throws Exception {
        when(alunoService.buscarPorNome("Teste")).thenReturn(Arrays.asList(aluno));
        mockMvc.perform(get("/alunos/buscar-por-nome").param("nome", "Teste"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Aluno Teste"));
    }

    @Test
    @DisplayName("Deve buscar por nome, retornar NO_CONTENT quando não encontrado")
    @WithMockUser
    void getAlunosPorNome_quandoAutenticadoENaoEncontrado_deveRetornarNoContent() throws Exception {
        when(alunoService.buscarPorNome("Inexistente")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/alunos/buscar-por-nome").param("nome", "Inexistente"))
                .andExpect(status().isNoContent());
    }
}
