package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.service.ReuniaoService1;
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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReuniaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReuniaoService1 reuniaoService;

    private Grupo grupo;
    private Reuniao reuniao1;
    private Reuniao reuniao2;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date data = sdf.parse("2025-10-23");
    private Time hora = Time.valueOf("14:00:00");

    public ReuniaoControllerTest() throws ParseException {
    }

    @BeforeEach
    void setup(){
        AreaDeAtuacao areaDeAtuacao = new AreaDeAtuacao(1L, "Tecnologia");
        Curso curso = new Curso(1L, "ADS", areaDeAtuacao);

        Aluno aluno1 =  new Aluno(1L, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno2 = new Aluno(2L, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno3  = new Aluno(3L, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);

        Mentor mentor = new Mentor();
        mentor.setNome("Romana Novaes");
        mentor.setId(1L);
        mentor.setAreaDeAtuacao(areaDeAtuacao);

        grupo = new Grupo(1L, "Grupo Ativo", StatusGrupo.ATIVO, aluno1, List.of(aluno1, aluno2, aluno3));

        reuniao1 = new Reuniao(1L, "Validar requisitos do projeto", data, hora, FormatoReuniao.ONLINE, StatusReuniao.ACEITO, mentor, grupo);
        reuniao2 = new Reuniao(2L, "Assinar documentos", data, hora, FormatoReuniao.PRESENCIAL, StatusReuniao.PENDENTE, mentor, grupo);
    }

    @Test
    @DisplayName("Deve retornar listas com todas as reuniões")
    @WithMockUser
    void buscarReunioes_deveRetornarListaDeReunioes() throws Exception {
        when(reuniaoService.findAll()).thenReturn(List.of(reuniao1, reuniao2));

        mockMvc.perform(get("/reunioes/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assunto").value("Validar requisitos do projeto"))
                .andExpect(jsonPath("$[1].assunto").value("Assinar documentos"));
    }

    @Test
    @DisplayName("Deve retornar reunião conforme o id")
    @WithMockUser
    void buscarReuniao_deveRetornarReuniao() throws Exception{
        when(reuniaoService.findById(1L)).thenReturn(reuniao1);

        mockMvc.perform(get("/reunioes/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assunto").value("Validar requisitos do projeto"));
    }

    @Test
    @DisplayName("Deve retornar erro quando Id da reunião inexistente")
    @WithMockUser
    void buscarReuniao_quandoIdInexistente_deveRetornarErro() throws Exception {
        when(reuniaoService.findById(-1L)).thenThrow(new RuntimeException("Reunião não encontrada"));

        mockMvc.perform(get("/reunioes/findById/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Reunião não encontrada"));
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões de acordo com o grupo")
    @WithMockUser
    void buscarReunioes_deveRetornarReunioesDoGrupo() throws Exception {
        when(reuniaoService.findAllByGrupo(1L)).thenReturn(List.of(reuniao1, reuniao2));

        mockMvc.perform(get("/reunioes/findByGrupo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].assunto").value("Assinar documentos"));
    }

    @Test
    @DisplayName("Deve retornar erro quando Id de grupo inexistente")
    @WithMockUser
    void buscarReunioes_quandoIdGrupoInexistente_deveRetornarErro() throws Exception {
        when(reuniaoService.findAllByGrupo(-1L)).thenThrow(new RuntimeException("Grupo não encontrado"));

        mockMvc.perform(get("/reunioes/findByGrupo/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Grupo não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões de acordo com o mentor")
    @WithMockUser
    void buscarReunioes_deveRetornarReunioesDoMentor() throws Exception {
        when(reuniaoService.findAllByMentor(1L)).thenReturn(List.of(reuniao1, reuniao2));

        mockMvc.perform(get("/reunioes/findByMentor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assunto").value("Validar requisitos do projeto"));
    }

    @Test
    @DisplayName("Deve retornar erro quando Id de mentor inexistente")
    @WithMockUser
    void buscarReunioes_quandoIdMentorInexistente_deveRetornarErro() throws Exception {
        when(reuniaoService.findAllByMentor(-1L)).thenThrow(new RuntimeException("Mentor não encontrado"));

        mockMvc.perform(get("/reunioes/findByMentor/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mentor não encontrado"));
    }

    @Test
    @DisplayName("Deve salvar nova reunião e retornar status created")
    @WithMockUser
    void saveReuniao_quandoInformacoesCorretas_deveRetornarSucesso() throws Exception {
        ReuniaoDTO reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora,FormatoReuniao.ONLINE, 1L, 1L);
        when(reuniaoService.save(any(ReuniaoDTO.class))).thenReturn("Reunião enviada para análise");

        mockMvc.perform(post("/reunioes/save")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reuniaoDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Reunião enviada para análise"));
    }

    @Test
    @DisplayName("Deve salvar mudanças feitas na reunião e retornar status status OK")
    @WithMockUser
    void updateReuniao_quandoInformacoesCorretas_deveRetornarMensagemDeSucesso() throws Exception{
        Reuniao reuniaoUpdate = new Reuniao();
        reuniaoUpdate.setAssunto("Novo assunto");

        when(reuniaoService.update(eq(1L), any(Reuniao.class))).thenReturn("Reunião atualizada com sucesso!");

        mockMvc.perform(put("/reunioes/update/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reuniaoUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reunião atualizada com sucesso!"));
    }

    @Test
    @DisplayName("Deve alterar o status da reuniao quando for aceita ou cancelada e retornar status 200")
    @WithMockUser
    void updateReuniao_deveRetornarMensagemDeSucessoAoAlterarStatus() throws Exception {
        when(reuniaoService.aceitarReuniao(1L, StatusReuniao.ACEITO)).thenReturn("Reunião aceita com sucesso!");

        mockMvc.perform(put("/reunioes/confirmarReuniao/1/status/aceito")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Reunião aceita com sucesso!"));
    }

    @Test
    @DisplayName("Deve deletar Reuniao, retornar mensagem de sucesso e status OK")
    @WithMockUser
    void deleteReuniao_deveRetornarMensagemDeSucesso() throws Exception{
        when(reuniaoService.delete(1L)).thenReturn("Reunião deletada cim sucesso!");

        mockMvc.perform(delete("/reunioes/delete/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Reunião deletada cim sucesso!"));
    }

}
