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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReuniaoServiceTests {

    @InjectMocks
    private ReuniaoService1 reuniaoService;

    @Mock
    private ReuniaoRepository reuniaoRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private MentorRepository mentorRepository;

    private Mentor mentor;
    private Mentor mentorDif;
    private Grupo grupo;
    private Reuniao reuniaoAceita;
    private Reuniao reuniaoPendente;
    private ReuniaoDTO reuniaoDTO;
    private Aluno aluno1;
    private Aluno aluno2;
    private Aluno aluno3;
    private Date data;
    private Time hora;
    private AreaDeAtuacao areaTecnologia;
    private AreaDeAtuacao areaSaude;
    private Curso curso;


    @BeforeEach
    void setup() {
        // Inicialização de dados
        LocalDate localDate = LocalDate.of(2025, 10, 23);
        this.data = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.hora = Time.valueOf(LocalTime.of(14, 0, 0));

        areaTecnologia = new AreaDeAtuacao(1L, "Tecnologia");
        areaSaude = new AreaDeAtuacao(2L, "Saúde");
        curso = new Curso(1L, "Engenharia de Software", areaTecnologia);

        aluno1 = new Aluno(1L, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno2 = new Aluno(2L, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno3 = new Aluno(3L, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);

        // O serviço de Reunião não deve se preocupar com os "sets" de grupos/alunos
        grupo = new Grupo(1L, "Grupo Ativo", StatusGrupo.ATIVO, aluno1, List.of(aluno1, aluno2, aluno3));

        mentor = new Mentor();
        mentor.setId(1L);
        mentor.setNome("Romana Novaes");
        mentor.setAreaDeAtuacao(areaTecnologia);

        mentorDif = new Mentor();
        mentorDif.setId(2L);
        mentorDif.setNome("Vitoria");
        mentorDif.setAreaDeAtuacao(areaSaude);

        reuniaoAceita = new Reuniao(1L, "Validar requisitos do projeto", data, hora, FormatoReuniao.ONLINE, StatusReuniao.ACEITO, mentor, grupo);
        reuniaoPendente = new Reuniao(2L, "Assinar documentos", data, hora, FormatoReuniao.PRESENCIAL, StatusReuniao.PENDENTE, mentor, grupo);
    }

    // --- Testes de Busca (Find) ---

    @Test
    @DisplayName("BUSCA: Deve retornar lista com todas as reuniões")
    void buscarReunioes_deveRetornarListaDeReunioes() {
        when(reuniaoRepository.findAll()).thenReturn(List.of(reuniaoAceita, reuniaoPendente));

        List<Reuniao> retorno = reuniaoService.findAll();

        assertNotNull(retorno);
        assertEquals(2, retorno.size());
        verify(reuniaoRepository).findAll();
    }

    @Test
    @DisplayName("BUSCA: Deve retornar lista com todas as reuniões do mentor pelo ID")
    void buscarReunioesMentor_deveRetornarListaDeReunioesDoMentor() {
        when(reuniaoRepository.findAllMentor(mentor.getId())).thenReturn(List.of(reuniaoAceita, reuniaoPendente));

        List<Reuniao> retorno = reuniaoService.findAllByMentor(mentor.getId());

        assertFalse(retorno.isEmpty());
        assertEquals(2, retorno.size());
        verify(reuniaoRepository).findAllMentor(mentor.getId());
    }

    @Test
    @DisplayName("BUSCA: Deve retornar lista com todas as reuniões do grupo pelo ID")
    void buscarReunioesGrupo_deveRetornarListaDeReunioesDoGrupo() {
        when(reuniaoRepository.findAllGrupo(grupo.getId())).thenReturn(List.of(reuniaoAceita, reuniaoPendente));

        List<Reuniao> retorno = reuniaoService.findAllByGrupo(grupo.getId());

        assertFalse(retorno.isEmpty());
        assertEquals(2, retorno.size());
        verify(reuniaoRepository).findAllGrupo(grupo.getId());
    }

    @Test
    @DisplayName("BUSCA: Deve retornar a reunião quando o Id existe")
    void buscarReuniao_quandoIdExiste_deveRetornarReuniao() {
        when(reuniaoRepository.findById(reuniaoAceita.getId())).thenReturn(Optional.of(reuniaoAceita));

        Reuniao retorno = reuniaoService.findById(reuniaoAceita.getId());

        assertNotNull(retorno);
        assertEquals("Validar requisitos do projeto", retorno.getAssunto());
        verify(reuniaoRepository).findById(reuniaoAceita.getId());
    }

    @Test
    @DisplayName("BUSCA: Deve retornar erro quando o Id for inexistente")
    void buscarReuniao_quandoIdInexistente_deveRetornarErro() {
        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reuniaoService.findById(-1L);
        });

        assertEquals("Reunião não encontrada com id -1", exception.getMessage());
        verify(reuniaoRepository).findById(-1L);
    }

    // --- Testes de Cadastro (Save) ---

    @Test
    @DisplayName("CADASTRO: Deve cadastrar e retornar sucesso quando informações corretas")
    void saveReuniao_quandoCorreto_deveRetornarSucesso() {
        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora, FormatoReuniao.ONLINE, mentor.getId(), grupo.getId());

        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(alunoRepository.findAllByGrupoId(grupo.getId())).thenReturn(List.of(aluno1, aluno2, aluno3));

        // Mock o save e capture o argumento para verificação (opcional, mas boa prática)
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> {
            Reuniao savedReuniao = invocation.getArgument(0);
            savedReuniao.setId(3L); // Simula o ID gerado no DB
            return savedReuniao;
        });

        String retorno = reuniaoService.save(reuniaoDTO);

        assertEquals("Solicitação de reunião enviada", retorno);
        verify(reuniaoRepository).save(any(Reuniao.class));
    }

    @Test
    @DisplayName("CADASTRO: Deve retornar erro quando id do Grupo não existe")
    void saveReuniao_quandoIdGrupoIncorreto_deveRetornarErro() {
        Long idGrupoInexistente = -1L;
        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora, FormatoReuniao.ONLINE, mentor.getId(), idGrupoInexistente);

        when(grupoRepository.findById(idGrupoInexistente)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.save(reuniaoDTO);
        });

        assertEquals("Grupo não encontrado", exception.getMessage());
        verify(grupoRepository).findById(idGrupoInexistente);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("CADASTRO: Deve retornar erro quando mentor não encontrado")
    void saveReuniao_quandoIdMentorIncorreto_deveRetornarErro() {
        Long idMentorInexistente = -1L;
        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora, FormatoReuniao.ONLINE, idMentorInexistente, grupo.getId());

        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(mentorRepository.findById(idMentorInexistente)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.save(reuniaoDTO);
        });

        assertEquals("Mentor não encontrado", exception.getMessage());
        verify(mentorRepository).findById(idMentorInexistente);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("CADASTRO: Deve retornar erro quando área de atuação do mentor for diferente da dos alunos do grupo")
    void saveReuniao_quandoAreaDeAtuacaoDoMentorDiferenteDoGrupo_deveRetornarErro() {
        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora, FormatoReuniao.ONLINE, mentorDif.getId(), grupo.getId());

        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(mentorRepository.findById(mentorDif.getId())).thenReturn(Optional.of(mentorDif));
        when(alunoRepository.findAllByGrupoId(grupo.getId())).thenReturn(List.of(aluno1, aluno2, aluno3));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.save(reuniaoDTO);
        });

        assertEquals("A área de atuação de pelo menos um aluno não corresponde à do mentor.", exception.getMessage());
        verify(alunoRepository).findAllByGrupoId(grupo.getId());
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("CADASTRO: Deve retornar erro quando a área de atuação de um aluno for nula")
    void saveReuniao_quandoAreaAlunoNula_deveRetornarErro() {
        Aluno alunoNulo = new Aluno(4L, "Teste Nulo", 1004, "senha", "teste@gmail.com", new Curso(2L, "Curso Nulo", null), StatusAlunoGrupo.ATIVO);

        reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora, FormatoReuniao.ONLINE, mentor.getId(), grupo.getId());

        when(grupoRepository.findById(grupo.getId())).thenReturn(Optional.of(grupo));
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(alunoRepository.findAllByGrupoId(grupo.getId())).thenReturn(List.of(aluno1, alunoNulo)); // Aluno com área nula

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.save(reuniaoDTO);
        });

        assertEquals("A área de atuação de pelo menos um aluno não corresponde à do mentor.", exception.getMessage());
        verify(alunoRepository).findAllByGrupoId(grupo.getId());
    }

    // --- Testes de Atualização (Update) ---

    @Test
    @DisplayName("UPDATE: Deve retornar erro quando Id da reunião for incorreto")
    void update_quandoIdDaReuniaoIncorreto_deveRetornarErro() {
        Reuniao reuniaoAtualizada = new Reuniao();
        reuniaoAtualizada.setAssunto("Novo assunto teste");

        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.update(-1L, reuniaoAtualizada);
        });

        assertEquals("Reunião não encontrada", exception.getMessage());
        verify(reuniaoRepository).findById(-1L);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("UPDATE: Deve retornar erro quando a reunião não tiver o status PENDENTE")
    void update_quandoStatusReuniaoDiferenteDePendente_deveRetornarErro() {
        Reuniao reuniaoUpdates = new Reuniao();
        reuniaoUpdates.setAssunto("Novo assunto teste");

        // reuniaoAceita tem StatusReuniao.ACEITO
        when(reuniaoRepository.findById(reuniaoAceita.getId())).thenReturn(Optional.of(reuniaoAceita));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.update(reuniaoAceita.getId(), reuniaoUpdates);
        });

        assertEquals("Operação não permitida. A reunião já foi avaliada pelo solicitado", exception.getMessage());
        verify(reuniaoRepository).findById(reuniaoAceita.getId());
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("UPDATE: Deve atualizar a reunião com novos dados e retornar sucesso")
    void update_quandoInformacoesCorretas_deveRetornarSucesso() {
        // Objeto com os campos a serem atualizados
        Reuniao reuniaoUpdates = new Reuniao();
        reuniaoUpdates.setAssunto("Novo assunto teste");
        reuniaoUpdates.setData(data);
        reuniaoUpdates.setFormatoReuniao(FormatoReuniao.PRESENCIAL);

        when(reuniaoRepository.findById(reuniaoPendente.getId())).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoPendente);

        String retorno = reuniaoService.update(reuniaoPendente.getId(), reuniaoUpdates);

        assertEquals("Reunião atualizada e reenviada para aprovação", retorno);
        verify(reuniaoRepository).save(reuniaoPendente);
        assertEquals("Novo assunto teste", reuniaoPendente.getAssunto(), "O assunto deve ter sido atualizado.");
        assertEquals(StatusReuniao.PENDENTE, reuniaoPendente.getStatusReuniao(), "O status deve ser redefinido para PENDENTE.");
    }

    @Test
    @DisplayName("UPDATE: Deve atualizar reunião, preenchendo apenas campos não nulos, e retornar sucesso")
    void update_quandoCamposNulos_deveAtualizarComSucesso() {
        Reuniao reuniaoUpdates = new Reuniao(); // Todos os campos de update nulos
        reuniaoUpdates.setAssunto("Novo Assunto Nulo Teste"); // Apenas este campo será atualizado

        when(reuniaoRepository.findById(reuniaoPendente.getId())).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoPendente);

        String retorno = reuniaoService.update(reuniaoPendente.getId(), reuniaoUpdates);

        assertEquals("Reunião atualizada e reenviada para aprovação", retorno);
        verify(reuniaoRepository).save(reuniaoPendente);
        assertEquals("Novo Assunto Nulo Teste", reuniaoPendente.getAssunto(), "O assunto deve ter sido atualizado.");
        assertEquals(reuniaoPendente.getData(), reuniaoPendente.getData(), "A data deve permanecer inalterada se nula no update.");
    }

    // --- Testes de Aceitação/Rejeição (Aceitar) ---

    @Test
    @DisplayName("ACEITAR: Deve retornar erro quando id da reunião for inexistente")
    void aceitarReuniao_quandoIdReuniaoInexistente_deveRetornarErro() {
        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.aceitarReuniao(-1L, StatusReuniao.ACEITO);
        });

        assertEquals("Reunião não encontrada", exception.getMessage());
        verify(reuniaoRepository).findById(-1L);
        verify(reuniaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("ACEITAR: Deve atualizar status da reunião e retornar mensagem de sucesso")
    void aceitarReuniao_quandoInformacoesCorretas_deveRetornarSucesso() {
        // reuniaoPendente tem StatusReuniao.PENDENTE
        when(reuniaoRepository.findById(reuniaoPendente.getId())).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoPendente);

        String retorno = reuniaoService.aceitarReuniao(reuniaoPendente.getId(), StatusReuniao.ACEITO);

        assertEquals("Status reunião: aceito", retorno);
        verify(reuniaoRepository).save(reuniaoPendente);
        assertEquals(StatusReuniao.ACEITO, reuniaoPendente.getStatusReuniao());
    }

    // --- Testes de Deleção (Delete) ---

    @Test
    @DisplayName("DELETE: Deve retornar erro quando id da reunião for inexistente")
    void deletarReuniao_quandoIdInexistente_deveRetornarErro() {
        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.delete(-1L);
        });

        assertEquals("Reunião não encontrada", exception.getMessage());
        verify(reuniaoRepository).findById(-1L);
        verify(reuniaoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("DELETE: Deve deletar reunião e retornar sucesso")
    void deletarReuniao_deveRetornarSucesso() {
        when(reuniaoRepository.findById(reuniaoAceita.getId())).thenReturn(Optional.of(reuniaoAceita));
        doNothing().when(reuniaoRepository).delete(reuniaoAceita);

        String retorno = reuniaoService.delete(reuniaoAceita.getId());

        assertEquals("Reunião deletada com sucesso", retorno);
        verify(reuniaoRepository).findById(reuniaoAceita.getId());
        verify(reuniaoRepository).delete(reuniaoAceita);
    }
}