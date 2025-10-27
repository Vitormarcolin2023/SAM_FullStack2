package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.service.ProjetoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ProjetoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProjetoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProjetoService projetoService;

    @MockitoBean
    TokenService tokenService;

    private AreaDeAtuacao areaTI;
    private Endereco enderecoCarlos;
    private Mentor mentorCarlos;
    private Aluno alunoAdmin;
    private Grupo grupoA;
    private Projeto projeto1;
    private Projeto projeto2;
    private List<Projeto> listaProjetos;

    @BeforeEach
    void setup() {

        // Área de Atuação
        areaTI = new AreaDeAtuacao(1L, "Tecnologia");

        // Endereço
        enderecoCarlos = new Endereco();
        enderecoCarlos.setId(1L);
        enderecoCarlos.setBairro("Centro");
        enderecoCarlos.setCep("00000-000");
        enderecoCarlos.setCidade("São Paulo");
        enderecoCarlos.setEstado("SP");
        enderecoCarlos.setNumero("123");
        enderecoCarlos.setRua("Rua X");

        // Mentor
        mentorCarlos = new Mentor();
        mentorCarlos.setId(1L);
        mentorCarlos.setNome("Carlos Silva");
        mentorCarlos.setCpf("12345678901");
        mentorCarlos.setEmail("carlos@gmail.com");
        mentorCarlos.setSenha("senha123");
        mentorCarlos.setTelefone("11999999999");
        mentorCarlos.setTempoDeExperiencia("5 anos");
        mentorCarlos.setStatusMentor(StatusMentor.ATIVO);
        mentorCarlos.setTipoDeVinculo(TipoDeVinculo.CLT);
        mentorCarlos.setAreaDeAtuacao(areaTI);
        mentorCarlos.setEndereco(enderecoCarlos);

        // Aluno Admin
        alunoAdmin = new Aluno();
        alunoAdmin.setId(1L);
        alunoAdmin.setNome("Joana Silveira");
        alunoAdmin.setEmail("joana@gmail.com");
        alunoAdmin.setSenha("senha123");

        // Grupo
        grupoA = new Grupo();
        grupoA.setId(1L);
        grupoA.setNome("Grupo A");
        grupoA.setAlunoAdmin(alunoAdmin);
        grupoA.setStatusGrupo(StatusGrupo.ATIVO);

        // Projetos
        projeto1 = new Projeto();
        projeto1.setId(1L);
        projeto1.setNomeDoProjeto("Sistema Escolar");
        projeto1.setDescricao("Gerenciamento de alunos");
        projeto1.setAreaDeAtuacao(areaTI);
        projeto1.setDataInicioProjeto(LocalDate.of(2024, 1, 1));
        projeto1.setDataFinalProjeto(LocalDate.of(2024, 6, 30));
        projeto1.setPeriodo("1° Periodo");
        projeto1.setMentor(mentorCarlos);
        projeto1.setStatusProjeto("Em Andamento");
        projeto1.setGrupo(grupoA);

        projeto2 = new Projeto();
        projeto2.setId(2L);
        projeto2.setNomeDoProjeto("App Financeiro");
        projeto2.setDescricao("Controle financeiro pessoal");
        projeto2.setAreaDeAtuacao(areaTI);
        projeto2.setDataInicioProjeto(LocalDate.of(2024, 2, 1));
        projeto2.setDataFinalProjeto(LocalDate.of(2024, 8, 31));
        projeto2.setPeriodo("Integral");
        projeto2.setMentor(mentorCarlos);
        projeto2.setStatusProjeto("Planejamento");
        projeto2.setGrupo(grupoA);

        listaProjetos = Arrays.asList(projeto1, projeto2);
    }

    @Test
    @DisplayName("Deve retornar todos os projetos")
    void listAll_deveRetornarTodosOsProjetos() throws Exception {
        when(projetoService.listAll()).thenReturn(listaProjetos);

        mockMvc.perform(get("/projetos/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(listaProjetos.size()))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$[1].nomeDoProjeto").value("App Financeiro"));
    }

    @Test
    @DisplayName("Deve retornar projeto")
    void findById_quandoIdValido_deveRetornarProjeto() throws Exception {
        when(projetoService.findById(1L)).thenReturn(projeto1);

        mockMvc.perform(get("/projetos/findById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$.descricao").value("Gerenciamento de alunos"))
                .andExpect(jsonPath("$.statusProjeto").value("Em Andamento"));

    }

    @Test
    @DisplayName("Deve retornar projetos filtrando por nome")
    void buscarPorNome_deveRetornarProjetos() throws Exception {
        String nome = "Sistema Escolar";

        when(projetoService.buscarPorNome(nome)).thenReturn(Arrays.asList(projeto1));

        mockMvc.perform(get("/projetos/buscar-por-nome")
                        .param("nome", nome)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"));
    }

    @Test
    @DisplayName("Deve retornar projetos filtrando por área de atuação")
    void buscarPorAtuacao_quandoNomeValido_deveRetornarProjetos() throws Exception {
        AreaDeAtuacao areaTI = new AreaDeAtuacao();
        areaTI.setNome("Tecnologia");

        List<Projeto> projetosTI = Arrays.asList(projeto1, projeto2);

        when(projetoService.buscarPorAreaAtuacao(areaTI)).thenReturn(Arrays.asList(projeto1, projeto2));
        mockMvc.perform(get("/projetos/buscar-por-atuacao")
                        .param("areaNome", "Tecnologia")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$[1].nomeDoProjeto").value("App Financeiro"));
    }

    @Test
    @DisplayName("Deve salvar um projeto ")
    void save_deveSalvarProjeto() throws Exception {

        // Área de Atuação
        AreaDeAtuacao areaTI = new AreaDeAtuacao(1L, "Tecnologia");

        // Curso do Aluno
        Curso cursoTI = new Curso();
        cursoTI.setId(1L);
        cursoTI.setNome("Tecnologia");
        cursoTI.setAreaDeAtuacao(areaTI);

        // Aluno
        Aluno alunoAdmin = new Aluno();
        alunoAdmin.setId(1L);
        alunoAdmin.setNome("Joana Silveira");
        alunoAdmin.setEmail("joana@gmail.com");
        alunoAdmin.setSenha("senha123");
        alunoAdmin.setCurso(cursoTI);

        // Grupo
        Grupo grupoB = new Grupo();
        grupoB.setId(1L);
        grupoB.setNome("Grupo A");
        grupoB.setAlunoAdmin(alunoAdmin);
        grupoB.setAlunos(Arrays.asList(alunoAdmin));
        grupoB.setStatusGrupo(StatusGrupo.ATIVO);

        // Projeto a ser salvo
        Projeto projeto4 = new Projeto();
        projeto4.setId(4L);
        projeto4.setNomeDoProjeto("Projeto Financeiro");
        projeto4.setDescricao("Controle de finanças pessoais");
        projeto4.setAreaDeAtuacao(areaTI);
        projeto4.setDataInicioProjeto(LocalDate.of(2024, 5, 1));
        projeto4.setDataFinalProjeto(LocalDate.of(2024, 12, 31));
        projeto4.setPeriodo("2° Periodo");
        projeto4.setMentor(mentorCarlos);
        projeto4.setStatusProjeto("Ativo");
        projeto4.setGrupo(grupoB);

        when(projetoService.save(any(Projeto.class))).thenReturn(projeto4);

        String projetoJson = objectMapper.writeValueAsString(projeto4);

        mockMvc.perform(post("/projetos/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoProjeto").value("Projeto Financeiro"))
                .andExpect(jsonPath("$.descricao").value("Controle de finanças pessoais"))
                .andExpect(jsonPath("$.statusProjeto").value("Ativo"))
                .andExpect(jsonPath("$.grupo.nome").value("Grupo A"))
                .andExpect(jsonPath("$.areaDeAtuacao.nome").value("Tecnologia"));
    }

    @Test
    @DisplayName("Deve atualizar um projeto existente")
    void update_deveAtualizarProjeto() throws Exception {

        Projeto projetoAtualizado = new Projeto();
        projetoAtualizado.setId(projeto1.getId());
        projetoAtualizado.setNomeDoProjeto("Sistema Escolar Atualizado");
        projetoAtualizado.setDescricao("Gerenciamento de alunos atualizado");
        projetoAtualizado.setAreaDeAtuacao(projeto1.getAreaDeAtuacao());
        projetoAtualizado.setDataInicioProjeto(projeto1.getDataInicioProjeto());
        projetoAtualizado.setDataFinalProjeto(projeto1.getDataFinalProjeto());
        projetoAtualizado.setPeriodo(projeto1.getPeriodo());
        projetoAtualizado.setMentor(projeto1.getMentor());
        projetoAtualizado.setStatusProjeto("Concluído");
        projetoAtualizado.setGrupo(projeto1.getGrupo());

        when(projetoService.update(eq(1L), any(Projeto.class))).thenReturn(projetoAtualizado);

        String projetoJson = objectMapper.writeValueAsString(projetoAtualizado);

        mockMvc.perform(put("/projetos/update/{id}", projeto1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoProjeto").value("Sistema Escolar Atualizado"))
                .andExpect(jsonPath("$.descricao").value("Gerenciamento de alunos atualizado"))
                .andExpect(jsonPath("$.statusProjeto").value("Concluído"))
                .andExpect(jsonPath("$.grupo.nome").value("Grupo A"))
                .andExpect(jsonPath("$.areaDeAtuacao.nome").value("Tecnologia"));
    }
    @Test
    @DisplayName("Deve deletar um projeto")
    void delete_deveRemoverProjeto() throws Exception {
        Long projetoId = 1L;
        Mockito.doNothing().when(projetoService).delete(projetoId);

        mockMvc.perform(delete("/projetos/delete/{id}", projetoId))
                .andExpect(status().isOk())
                .andExpect(content().string("Projeto excluído com sucesso"));
    }
    @Test
    @DisplayName("Deve retornar projetos de um mentor específico")
    void findByMentor_deveRetornarProjetosDoMentor() throws Exception {
        Long mentorId = 1L;

        when(projetoService.findByMentor(mentorId)).thenReturn(Arrays.asList(projeto1, projeto2));

        mockMvc.perform(get("/projetos/mentor/{id}", mentorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$[1].nomeDoProjeto").value("App Financeiro"));
    }
    @Test
    @DisplayName("Deve retornar projetos de um professor específico")
    void buscarProjetosPorProfessor_deveRetornarProjetos() throws Exception {
        Long professorId = 1L;

        // Mock do service para retornar projetos
        when(projetoService.buscarProjetosPorProfessor(professorId))
                .thenReturn(Arrays.asList(projeto1, projeto2));

        mockMvc.perform(get("/projetos/professor/{professorId}", professorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$[1].nomeDoProjeto").value("App Financeiro"));


    }
    @Test
    @DisplayName("Deve retornar 204 quando não houver projetos para o professor")
    void buscarProjetosPorProfessor_quandoNaoExistir_deveRetornarNoContent() throws Exception {
        Long professorId = 9L;

        when(projetoService.buscarProjetosPorProfessor(professorId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/projetos/professor/{professorId}", professorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}