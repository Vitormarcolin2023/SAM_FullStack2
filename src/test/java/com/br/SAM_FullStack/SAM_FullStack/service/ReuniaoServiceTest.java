package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ReuniaoServiceTest {

    @Autowired
    private ReuniaoService1 reuniaoService;

    @MockitoBean
    private ReuniaoRepository reuniaoRepository;

    @MockitoBean
    private GrupoRepository grupoRepository;

    @MockitoBean
    private AlunoRepository alunoRepository;

    @MockitoBean
    private MentorRepository mentorRepository;

    private Mentor mentor;
    private Grupo grupo;
    private Reuniao reuniao1;
    private Reuniao reuniao2;
    private ReuniaoDTO reuniaoDTO;
    private Aluno aluno1;
    private Aluno aluno2;
    private Aluno aluno3;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date data = sdf.parse("2025-10-23");
    private Time hora = Time.valueOf("14:00:00");

    public ReuniaoServiceTest() throws ParseException {
    }

    @BeforeEach
    void setup()  {
        AreaDeAtuacao areaDeAtuacao = new AreaDeAtuacao(1L, "Tecnologia");
        Curso curso = new Curso(1L, "Engenharia de Software", areaDeAtuacao);

        aluno1 =  new Aluno(1L, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno2 = new Aluno(2L, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno3  = new Aluno(3L, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);

        grupo = new Grupo(1L, "Grupo Ativo", StatusGrupo.ATIVO, aluno1, List.of(aluno1, aluno2, aluno3));
        aluno1.setGrupos(List.of(grupo));
        aluno2.setGrupos(List.of(grupo));
        aluno3.setGrupos(List.of(grupo));

        mentor = new Mentor();
        mentor.setNome("Romana Novaes");
        mentor.setId(1L);
        mentor.setAreaDeAtuacao(areaDeAtuacao);

        reuniao1 = new Reuniao(1L, "Validar requisitos do projeto", data, hora, FormatoReuniao.ONLINE, StatusReuniao.ACEITO, mentor, grupo);
        reuniao2 = new Reuniao(2L, "Assinar documentos", data, hora, FormatoReuniao.PRESENCIAL, StatusReuniao.PENDENTE, mentor, grupo);
    }

    @Test
    @DisplayName("Deve retornar lista com todas as reuniões")
    void buscarReunioes_deveRetornarListaDeReunioes(){
        when(reuniaoRepository.findAll()).thenReturn(List.of(reuniao1, reuniao2));

        List<Reuniao> retorno = reuniaoService.findAll();
        assertEquals(2, retorno.size());
        assertEquals(1L, retorno.get(0).getId());
    }

    @Test
    @DisplayName("Deve retornar lista com todos as reuniões do mentor")
    void buscarReunioesMentor_deveRetornarListaDeReunioesDoMentor(){
        when(reuniaoRepository.findAllMentor(1L)).thenReturn(List.of(reuniao1, reuniao2));

        List<Reuniao> retorno = reuniaoService.findAllByMentor(1L);
        assertFalse(retorno.isEmpty(), "A lista não deve estar vazia");
        assertEquals(2, retorno.size());
    }

    @Test
    @DisplayName("Deve retornar lista com todas as reuniões do grupo")
    void buscarReunioesGrupo_deveRetornarListaDeReunioesDoGrupo(){
        when(reuniaoRepository.findAllGrupo(1L)).thenReturn(List.of(reuniao1, reuniao2));

        List<Reuniao> retorno = reuniaoService.findAllByGrupo(1L);
        assertFalse(retorno.isEmpty());
        assertEquals(2, retorno.size());
    }

    @Test
    @DisplayName("Deve retornar a reunião com o Id")
    void buscarReuniao_quandoIdExiste_deveRetornarReuniao(){
        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniao1));

        Reuniao retorno = reuniaoService.findById(1L);
        assertEquals("Validar requisitos do projeto", retorno.getAssunto());
    }

    @Test
    @DisplayName("Deve retornaar erro quando o Id passado for incorreto")
    void buscarReuniao_quandoIdInexistente_deveRetornarErro(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->{
           reuniaoService.findById(-1L);
        });
        assertEquals("Reunião não encontrada com id -1", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando id do Grupo não existe")
    void saveReuniao_quandoIdGrupoIncorreto_deveRetornarErro(){
        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora,FormatoReuniao.ONLINE, 1L, -1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->{
           reuniaoService.save(reuniaoDTO);
        });
        assertEquals("Grupo não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando mentor não encontrado")
    void saveReuniao_quandoIdMentorIncorreto_deveRetornarErro(){
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora,FormatoReuniao.ONLINE, -1L, 1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->{
           reuniaoService.save(reuniaoDTO);
        });
        assertEquals("Mentor não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando a área de atuação do mentor for diferente da dos alunos do grupo")
    void saveReuniao_quandoAreaDeAtuacaoDoMentorDiferenteDoGrupo_deveRetornarErro(){
        Mentor mentorDif = new Mentor();
        mentorDif.setId(2L);
        mentorDif.setNome("Vitoria");
        mentorDif.setAreaDeAtuacao(new AreaDeAtuacao(2L, "Saúde"));

        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora,FormatoReuniao.ONLINE, 2L, 1L);
        when(alunoRepository.findAllByGrupoId(1L)).thenReturn(List.of(aluno1, aluno2, aluno3));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        when(mentorRepository.findById(2L)).thenReturn(Optional.of(mentorDif));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
           reuniaoService.save(reuniaoDTO);
        });
        assertEquals("A área de atuação de pelo menos um aluno não corresponde à do mentor.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar mensgaem de sucesso quando informações corretas e reunião cadastrada")
    void saveReuniao_quandoCorreto_deveRetornarSucesso(){
        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora,FormatoReuniao.ONLINE, 1L, 1L);

        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(alunoRepository.findAllByGrupoId(1L)).thenReturn(List.of(aluno1, aluno2, aluno3));

        Reuniao novaReuniao = new Reuniao(3L, "nova reuniao", data, hora, FormatoReuniao.ONLINE, StatusReuniao.PENDENTE, mentor, grupo);

        when(reuniaoRepository.save(novaReuniao)).thenReturn(novaReuniao);

        String retorno = reuniaoService.save(reuniaoDTO);
        assertEquals("Solicitação de reunião enviada", retorno);
    }

    @Test
    @DisplayName("Deve retornar erro quando Id da reunião for incorreto")
    void update_quandoIdDaReuniaoIncorreto_deveRetornarErro(){
        Reuniao reuniaoAtualizada = new Reuniao();
        reuniaoAtualizada.setAssunto("Novo assunto teste");
        IllegalStateException exception = assertThrows(IllegalStateException.class, ()->{
           reuniaoService.update(-1L, reuniaoAtualizada);
        });
        assertEquals("Reunião não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando a reunião não tiver o status pendente")
    void update_quandoStatusReuniaoDiferenteDePendente_deveRetornarErro(){
        Reuniao reuniao = new Reuniao();
        reuniao.setAssunto("Novo assunto teste");

        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniao1));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->{
           reuniaoService.update(1L, reuniao);
        });
        assertEquals("Operação não permitida. A reunião já foi avaliada pelo solicitado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso quando informações corretas")
    void update_quandoInformacoesCorretas_deveRetornarSucesso(){
        Reuniao reuniao = new Reuniao();
        reuniao.setAssunto("Novo assunto teste");
        reuniao.setData(data);
        reuniao.setHora(hora);
        reuniao.setFormatoReuniao(FormatoReuniao.PRESENCIAL);

        when(reuniaoRepository.findById(2L)).thenReturn(Optional.of(reuniao2));
        when(reuniaoRepository.save(reuniao)).thenReturn(reuniao);

        String retorno = reuniaoService.update(2L, reuniao);
        assertEquals("Reunião atualizada e reenviada para aprovação", retorno);
    }

    @Test
    @DisplayName("Deve retornar erro quando não encontrar a reunião")
    void aceitarReuniao_quandoIdReuniaoInexistente_deveRetornarErro(){
        IllegalStateException exception = assertThrows(IllegalStateException.class, ()->{
           reuniaoService.aceitarReuniao(-1L, StatusReuniao.ACEITO);
        });
        assertEquals("Reunião não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve salvar novo status da reunião quando informações corretas e retornar mensagem de sucesso")
    void aceitarReuniao_quandoInformacoesCorretas_deveRetornarSucesso(){
        when(reuniaoRepository.findById(2L)).thenReturn(Optional.of(reuniao2));
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> {
            reuniao2.setStatusReuniao(StatusReuniao.ACEITO);
            return reuniao2;
        });

        String retorno = reuniaoService.aceitarReuniao(2L, StatusReuniao.ACEITO);
        assertEquals("Status reunião: aceito", retorno);
    }

    @Test
    @DisplayName("Deve retornar erro quando id da reunião for inexistente")
    void deletarReuniao_quandoIdInexistente_deveRetornarErro(){
        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniao1));
        IllegalStateException exception = assertThrows(IllegalStateException.class, ()->{
           reuniaoService.delete(-1L);
        });
        assertEquals("Reunião não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso ao deletar grupo")
    void deletarGrupo_deveRetornarSucesso(){
        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniao1));
        doNothing().when(reuniaoRepository).delete(reuniao1);
        String retorno = reuniaoService.delete(1L);
        assertEquals("Reunião deletada com sucesso", retorno);
    }

    @Test
    @DisplayName("Deve retornar erro quando a área de atuação de um aluno for nula")
    void saveReuniao_quandoAreaAlunoNula_deveRetornarErro(){
        Aluno alunoNulo = new Aluno(4L, "Teste Nulo", 1004, "senha", "teste@gmail.com", new Curso(2L, "Curso Nulo", null), StatusAlunoGrupo.ATIVO);
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        when(alunoRepository.findAllByGrupoId(1L)).thenReturn(List.of(aluno1, aluno2, alunoNulo));

        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora, FormatoReuniao.ONLINE, 1L, 1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.save(reuniaoDTO);
        });
        assertEquals("A área de atuação de pelo menos um aluno não corresponde à do mentor.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar reunião mesmo quando campos forem nulos")
    void update_quandoCamposNulos_deveAtualizarComSucesso(){
        Reuniao reuniaoAtualizada = new Reuniao(); // todos campos nulos
        when(reuniaoRepository.findById(2L)).thenReturn(Optional.of(reuniao2));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniao2);

        String retorno = reuniaoService.update(2L, reuniaoAtualizada);
        assertEquals("Reunião atualizada e reenviada para aprovação", retorno);
    }



}
