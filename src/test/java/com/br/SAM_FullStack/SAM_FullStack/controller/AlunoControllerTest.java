package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.CustomUserDetailsService;
import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Integração Web do AlunoController (Padrão @SpringBootTest)")
class AlunoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AlunoService alunoService;
    @MockitoBean
    TokenService tokenService;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    AreaDeAtuacao areaTI;
    Curso cursoADS;
    Aluno alunoJoana;
    Aluno alunoAnderson;

    final String TOKEN_VALIDO_JOANA = "token.valido.joana";
    final String TOKEN_COM_EMAIL_NAO_ENCONTRADO = "token.email.nao.existe";
    final String TOKEN_INVALIDO = "token-realmente-invalido";

    @BeforeEach
    void setup() {
        areaTI = new AreaDeAtuacao(1L, "Tecnologia");
        cursoADS = new Curso(1L, "ADS", areaTI);
        alunoJoana = new Aluno(1L, "Joana Silveira", 1001, "senha123", "joana@gmail.com", cursoADS, StatusAlunoGrupo.ATIVO);
        alunoAnderson = new Aluno(2L, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", cursoADS, StatusAlunoGrupo.ATIVO);

        when(tokenService.extractEmail(TOKEN_VALIDO_JOANA)).thenReturn(alunoJoana.getEmail());
        when(customUserDetailsService.loadUserByUsername(alunoJoana.getEmail())).thenReturn(alunoJoana);
        when(tokenService.validateToken(TOKEN_VALIDO_JOANA)).thenReturn(true);
        when(tokenService.extractEmail(TOKEN_COM_EMAIL_NAO_ENCONTRADO)).thenReturn("fantasma@mail.com");
        when(customUserDetailsService.loadUserByUsername("fantasma@mail.com"))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuário fantasma não encontrado"));
        when(tokenService.validateToken(TOKEN_COM_EMAIL_NAO_ENCONTRADO)).thenReturn(true);
        when(tokenService.extractEmail(TOKEN_INVALIDO)).thenThrow(new RuntimeException("Token Inválido Mock"));
        when(alunoService.findByEmail(alunoJoana.getEmail())).thenReturn(alunoJoana);
        when(alunoService.findByEmail("fantasma@mail.com")).thenReturn(null);
    }


    @Test
    @DisplayName("Deve retornar lista de alunos e status OK")
    @WithMockUser
    void findAll_quandoAutenticado_deveRetornarListaDeAlunosEStatusOK() throws Exception {
        when(alunoService.findAll()).thenReturn(Arrays.asList(alunoJoana, alunoAnderson));
        mockMvc.perform(get("/alunos/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Joana Silveira"))
                .andExpect(jsonPath("$[1].nome").value("Anderson Ribeiro"));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao buscar todos sem autenticação")
    void findAll_quandoNaoAutenticado_deveRetornarForbidden() throws Exception {
        // CORREÇÃO: Esperar 403
        mockMvc.perform(get("/alunos/findAll"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar aluno por ID e status OK (Caso de Sucesso)")
    @WithMockUser
    void findById_quandoAutenticadoEIdExistente_deveRetornarAlunoEStatusOK() throws Exception {
        when(alunoService.findById(1L)).thenReturn(alunoJoana);
        mockMvc.perform(get("/alunos/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Joana Silveira"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao buscar ID inexistente")
    @WithMockUser
    void findById_quandoAutenticadoEIdNaoExistente_deveRetornarBadRequest() throws Exception {
        // CORREÇÃO: O Handler deve pegar
        String msgErro = "Aluno não encontrado com ID: 99";
        when(alunoService.findById(99L)).thenThrow(new RuntimeException(msgErro));
        mockMvc.perform(get("/alunos/findById/99"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(msgErro));
    }

    @Test
    @DisplayName("Deve salvar aluno, retornar status CREATED e mensagem")
    @WithMockUser
    void save_quandoAutenticadoEValido_deveRetornarCreatedEMensagem() throws Exception {
        Aluno novoAluno = new Aluno(null, "Carlos Prado", 1003, "senhaNova", "carlos@mail.com", cursoADS, StatusAlunoGrupo.ATIVO);
        Aluno alunoSalvo = new Aluno(3L, "Carlos Prado", 1003, "senhaNovaEncriptada", "carlos@mail.com", cursoADS, StatusAlunoGrupo.ATIVO);
        when(alunoService.save(any(Aluno.class))).thenReturn(alunoSalvo);

        mockMvc.perform(post("/alunos/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoAluno)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Aluno cadastrado com sucesso!"));
    }

    @Test
    @DisplayName("Deve atualizar aluno, retornar status OK e aluno atualizado")
    @WithMockUser
    void update_quandoAutenticadoEValido_deveRetornarOKeAluno() throws Exception {
        Aluno alunoUpdateData = new Aluno();
        alunoUpdateData.setNome("Joana Silveira Atualizado");
        alunoUpdateData.setEmail("joana_nova@gmail.com");
        // CORREÇÃO: Adicionar campos obrigatórios para validação
        alunoUpdateData.setRa(1001);
        alunoUpdateData.setCurso(cursoADS);

        Aluno alunoAtualizado = new Aluno(1L, "Joana Silveira Atualizado", 1001, "senha123", "joana_nova@gmail.com", cursoADS, StatusAlunoGrupo.ATIVO);
        when(alunoService.update(eq(1L), any(Aluno.class))).thenReturn(alunoAtualizado);

        mockMvc.perform(put("/alunos/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunoUpdateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Joana Silveira Atualizado"))
                .andExpect(jsonPath("$.email").value("joana_nova@gmail.com"));
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
    @DisplayName("Deve salvar lista de alunos, retornar status CREATED e a lista salva")
    @WithMockUser
    void saveAll_quandoAutenticadoEValido_deveRetornarCreatedELista() throws Exception {
        List<Aluno> alunosParaSalvar = Arrays.asList(alunoJoana, alunoAnderson);
        when(alunoService.saveAll(anyList())).thenReturn(alunosParaSalvar);

        mockMvc.perform(post("/alunos/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alunosParaSalvar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("joana@gmail.com"))
                .andExpect(jsonPath("$[1].email").value("ander@gmail.com"));
    }

    @Test
    @DisplayName("Deve buscar por nome, retornar lista e status OK")
    @WithMockUser
    void getAlunosPorNome_quandoAutenticadoEEncontrado_deveRetornarListaEStatusOK() throws Exception {
        when(alunoService.buscarPorNome("Joana")).thenReturn(Arrays.asList(alunoJoana));
        mockMvc.perform(get("/alunos/buscar-por-nome").param("nome", "Joana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Joana Silveira"));
    }

    @Test
    @DisplayName("Deve buscar por nome, retornar NO_CONTENT quando não encontrado")
    @WithMockUser
    void getAlunosPorNome_quandoAutenticadoENaoEncontrado_deveRetornarNoContent() throws Exception {
        when(alunoService.buscarPorNome("Inexistente")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/alunos/buscar-por-nome").param("nome", "Inexistente"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar alunos ordenados por nome e status OK")
    @WithMockUser
    void getAlunoOrdenadosPorNome_quandoAutenticado_deveRetornarListaOrdenadaEStatusOK() throws Exception {
        when(alunoService.buscarTodosOrdenadoPorNome()).thenReturn(Arrays.asList(alunoAnderson, alunoJoana));
        mockMvc.perform(get("/alunos/ordenados-por-nome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome").value("Anderson Ribeiro"))
                .andExpect(jsonPath("$[1].nome").value("Joana Silveira"));
    }

    @Test
    @DisplayName("Deve retornar aluno por email e status OK")
    @WithMockUser
    void findByEmail_quandoAutenticadoEEmailExistente_deveRetornarAlunoEStatusOK() throws Exception {
        when(alunoService.findByEmail("joana@gmail.com")).thenReturn(alunoJoana);
        mockMvc.perform(get("/alunos/findByEmail").param("email", "joana@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joana@gmail.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao buscar email inexistente")
    @WithMockUser
    void findByEmail_quandoAutenticadoEEmailNaoExistente_deveRetornarBadRequest() throws Exception {
        String emailInexistente = "naoexiste@mail.com";
        String msgErro = "Aluno não encontrado com o E-mail: " + emailInexistente;
        when(alunoService.findByEmail(emailInexistente)).thenThrow(new RuntimeException(msgErro));
        mockMvc.perform(get("/alunos/findByEmail").param("email", emailInexistente)).andExpect(status().isBadRequest())
                .andExpect(content().string(msgErro));
    }

    @Test
    @DisplayName("Deve retornar lista de alunos por curso e status OK")
    @WithMockUser
    void findByCurso_quandoAutenticadoECursoExistente_deveRetornarListaEStatusOK() throws Exception {
        Long cursoId = 1L;
        when(alunoService.findByCurso(cursoId)).thenReturn(Arrays.asList(alunoJoana, alunoAnderson));
        mockMvc.perform(get("/alunos/findByCurso/" + cursoId)).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].curso.id").value(cursoId))
                .andExpect(jsonPath("$[1].curso.id").value(cursoId));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao buscar curso sem alunos")
    @WithMockUser
    void findByCurso_quandoAutenticadoECursoSemAlunos_deveRetornarBadRequest() throws Exception {
        Long cursoId = 99L;
        String msgErro = "Nenhum aluno encontrado nesse curso";
        when(alunoService.findByCurso(cursoId)).thenThrow(new RuntimeException(msgErro));
        mockMvc.perform(get("/alunos/findByCurso/" + cursoId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(msgErro));
    }

    @Test
    @DisplayName("Deve retornar perfil do aluno e status OK (com token válido)")
    void getAlunoProfile_quandoTokenValido_deveRetornarAlunoEStatusOK() throws Exception {
        mockMvc.perform(get("/alunos/me").header("Authorization", "Bearer " + TOKEN_VALIDO_JOANA))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value(alunoJoana.getEmail()));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando header Authorization ausente")
    void getAlunoProfile_quandoHeaderAusente_deveRetornarForbidden() throws Exception {
        mockMvc.perform(get("/alunos/me")).andExpect(status().isForbidden());
    }
}