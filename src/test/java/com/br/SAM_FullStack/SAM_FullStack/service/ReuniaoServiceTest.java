package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class ReuniaoServiceTest {

    @InjectMocks
    private ReuniaoService1 reuniaoService;

    @Mock
    private ReuniaoRepository reuniaoRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    private Projeto projeto;
    private Reuniao reuniaoAceita;
    private Reuniao reuniaoPendente;
    private ReuniaoDTO reuniaoDTO;
    private Date data;
    private LocalTime hora; // Alterado para LocalTime para bater com o DTO

    @BeforeEach
    void setup() {
        // Inicialização de Data e Hora
        LocalDate localDate = LocalDate.of(2025, 10, 23);
        this.data = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.hora = LocalTime.of(14, 0, 0); // LocalTime direto

        // Setup do Projeto
        projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNomeDoProjeto("SAM FullStack");
        projeto.setStatusProjeto(StatusProjeto.ATIVO);

        // Setup Reunião Aceita
        reuniaoAceita = new Reuniao();
        reuniaoAceita.setId(1L);
        reuniaoAceita.setAssunto("Validar requisitos do projeto");
        reuniaoAceita.setData(data);
        reuniaoAceita.setHora(hora); // Assumindo que o Model também usa LocalTime
        reuniaoAceita.setFormatoReuniao(FormatoReuniao.ONLINE);
        reuniaoAceita.setStatusReuniao(StatusReuniao.ACEITO);
        reuniaoAceita.setProjeto(projeto);

        // Setup Reunião Pendente
        reuniaoPendente = new Reuniao();
        reuniaoPendente.setId(2L);
        reuniaoPendente.setAssunto("Assinar documentos");
        reuniaoPendente.setData(data);
        reuniaoPendente.setHora(hora);
        reuniaoPendente.setFormatoReuniao(FormatoReuniao.PRESENCIAL);
        reuniaoPendente.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniaoPendente.setProjeto(projeto);
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
        when(reuniaoRepository.findAllMentor(1L)).thenReturn(List.of(reuniaoAceita));

        List<Reuniao> retorno = reuniaoService.findAllByMentor(1L);

        assertFalse(retorno.isEmpty());
        assertEquals(1, retorno.size());
        verify(reuniaoRepository).findAllMentor(1L);
    }

    @Test
    @DisplayName("BUSCA: Deve retornar lista com todas as reuniões do grupo pelo ID")
    void buscarReunioesGrupo_deveRetornarListaDeReunioesDoGrupo() {
        when(reuniaoRepository.findAllGrupo(1L)).thenReturn(List.of(reuniaoAceita));

        List<Reuniao> retorno = reuniaoService.findAllByGrupo(1L);

        assertFalse(retorno.isEmpty());
        assertEquals(1, retorno.size());
        verify(reuniaoRepository).findAllGrupo(1L);
    }

    @Test
    @DisplayName("BUSCA: Deve retornar lista com todas as reuniões do projeto pelo ID")
    void buscarReunioesProjeto_deveRetornarListaDeReunioesDoProjeto() {
        when(reuniaoRepository.findAllByProjetoId(projeto.getId())).thenReturn(List.of(reuniaoAceita, reuniaoPendente));

        List<Reuniao> retorno = reuniaoService.findAllByProjeto(projeto.getId());

        assertFalse(retorno.isEmpty());
        assertEquals(2, retorno.size());
        verify(reuniaoRepository).findAllByProjetoId(projeto.getId());
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
    @DisplayName("CADASTRO: Deve cadastrar e retornar sucesso quando projeto existe")
    void saveReuniao_quandoCorreto_deveRetornarSucesso() {
        // Construtor do DTO baseado na ordem dos campos da classe que você mandou
        reuniaoDTO = new ReuniaoDTO(
                "nova reunião",
                data,
                hora,
                FormatoReuniao.ONLINE,
                projeto.getId(),
                "MENTOR"
        );

        when(projetoRepository.findById(projeto.getId())).thenReturn(Optional.of(projeto));

        // Mock do save
        when(reuniaoRepository.save(any(Reuniao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String retorno = reuniaoService.save(reuniaoDTO);

        assertEquals("Solicitação de reunião enviada", retorno);
        verify(projetoRepository).findById(projeto.getId());
        verify(reuniaoRepository).save(any(Reuniao.class));
    }

    @Test
    @DisplayName("CADASTRO: Deve retornar erro quando Projeto não encontrado")
    void saveReuniao_quandoProjetoNaoEncontrado_deveRetornarErro() {
        reuniaoDTO = new ReuniaoDTO();
        reuniaoDTO.setProjeto_id(99L); // ID inexistente

        when(projetoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reuniaoService.save(reuniaoDTO);
        });

        assertEquals("Projeto não encontrado", exception.getMessage());
        verify(reuniaoRepository, never()).save(any());
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
    }

    @Test
    @DisplayName("UPDATE: Deve retornar erro quando a reunião não tiver o status PENDENTE")
    void update_quandoStatusReuniaoDiferenteDePendente_deveRetornarErro() {
        Reuniao reuniaoUpdates = new Reuniao();
        reuniaoUpdates.setAssunto("Novo assunto teste");

        when(reuniaoRepository.findById(reuniaoAceita.getId())).thenReturn(Optional.of(reuniaoAceita));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reuniaoService.update(reuniaoAceita.getId(), reuniaoUpdates);
        });

        assertEquals("Operação não permitida. A reunião já foi avaliada pelo solicitado", exception.getMessage());
    }

    @Test
    @DisplayName("UPDATE: Deve atualizar a reunião com novos dados e retornar sucesso")
    void update_quandoInformacoesCorretas_deveRetornarSucesso() {
        Reuniao reuniaoUpdates = new Reuniao();
        reuniaoUpdates.setAssunto("Novo assunto teste");
        reuniaoUpdates.setFormatoReuniao(FormatoReuniao.PRESENCIAL);

        when(reuniaoRepository.findById(reuniaoPendente.getId())).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoPendente);

        String retorno = reuniaoService.update(reuniaoPendente.getId(), reuniaoUpdates);

        assertEquals("Reunião atualizada e reenviada para aprovação", retorno);

        assertEquals("Novo assunto teste", reuniaoPendente.getAssunto());
        assertEquals(FormatoReuniao.PRESENCIAL, reuniaoPendente.getFormatoReuniao());
    }

    // --- Testes de Aceitação/Rejeição (Aceitar) ---

    @Test
    @DisplayName("ACEITAR: Deve aceitar reunião e retornar sucesso")
    void aceitarReuniao_quandoAceitar_deveRetornarSucesso() {
        when(reuniaoRepository.findById(reuniaoPendente.getId())).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoPendente);

        // Agora passando 'null' como motivo, pois é aceitação
        String retorno = reuniaoService.aceitarReuniao(reuniaoPendente.getId(), StatusReuniao.ACEITO, null);

        assertEquals("Status reunião: aceito", retorno);
        assertEquals(StatusReuniao.ACEITO, reuniaoPendente.getStatusReuniao());
        assertNull(reuniaoPendente.getMotivoRecusa());
    }

    @Test
    @DisplayName("ACEITAR: Deve recusar reunião com motivo e retornar sucesso")
    void aceitarReuniao_quandoRecusar_deveSalvarMotivo() {
        when(reuniaoRepository.findById(reuniaoPendente.getId())).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any(Reuniao.class))).thenReturn(reuniaoPendente);

        String motivo = "Horário indisponível";
        String retorno = reuniaoService.aceitarReuniao(reuniaoPendente.getId(), StatusReuniao.RECUSADO, motivo);

        assertEquals("Status reunião: recusado", retorno);
        assertEquals(StatusReuniao.RECUSADO, reuniaoPendente.getStatusReuniao());
        assertEquals(motivo, reuniaoPendente.getMotivoRecusa());
    }

    // --- Testes de Deleção (Delete) ---

    @Test
    @DisplayName("DELETE: Deve deletar reunião e retornar sucesso")
    void deletarReuniao_deveRetornarSucesso() {
        when(reuniaoRepository.findById(reuniaoAceita.getId())).thenReturn(Optional.of(reuniaoAceita));
        doNothing().when(reuniaoRepository).delete(reuniaoAceita);

        String retorno = reuniaoService.delete(reuniaoAceita.getId());

        assertEquals("Reunião deletada com sucesso", retorno);
        verify(reuniaoRepository).delete(reuniaoAceita);
    }
}