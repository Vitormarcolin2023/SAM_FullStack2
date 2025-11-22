package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusGrupo;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArquivamentoServiceTest {

    @InjectMocks
    private ArquivamentoService arquivamentoService;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    private Grupo grupoAtivo;
    private Grupo grupoArquivado;

    @BeforeEach
    void setup() {
        grupoAtivo = new Grupo();
        grupoAtivo.setId(1L);
        grupoAtivo.setNome("Grupo Ativo");
        grupoAtivo.setStatusGrupo(StatusGrupo.ATIVO);

        grupoArquivado = new Grupo();
        grupoArquivado.setId(2L);
        grupoArquivado.setNome("Grupo Arquivado");
        grupoArquivado.setStatusGrupo(StatusGrupo.ARQUIVADO);
    }

    @Test
    @DisplayName("Deve arquivar grupo ativo quando o mês for agosto")
    void ArquivarGrupo_quandoAgosto_deveArquivar() {
        Grupo grupoAtivo = new Grupo(1L, "Grupo Teste", StatusGrupo.ATIVO, null, List.of());

        when(grupoRepository.findByStatusGrupo(StatusGrupo.ATIVO))
                .thenReturn(List.of(grupoAtivo));

        LocalDate dataFake = LocalDate.of(2025, 8, 10);
        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(dataFake);
            arquivamentoService.verificaArquivamentoGrupos();
            assertEquals(StatusGrupo.ARQUIVADO, grupoAtivo.getStatusGrupo());
            verify(grupoRepository, times(1)).save(grupoAtivo);
        }
    }

    @Test
    @DisplayName("Não deve arquivar grupo ativo fora dos meses de arquivamento")
    void arquivarGrupo_ForaDeAgostoOuDezembro_naoDeveArquivar() {
        when(grupoRepository.findByStatusGrupo(StatusGrupo.ATIVO)).thenReturn(List.of(grupoAtivo));

        arquivamentoService.verificaArquivamentoGrupos();
        verify(grupoRepository, never()).save(any(Grupo.class));
        assertEquals(StatusGrupo.ATIVO, grupoAtivo.getStatusGrupo());
    }

    @Test
    @DisplayName("Não deve arquivar grupos que já estão arquivados")
    void ArquivarGrupos_quandoJaArquivados_NaoDeveArquivar() {
        when(grupoRepository.findByStatusGrupo(StatusGrupo.ATIVO)).thenReturn(List.of(grupoArquivado));

        arquivamentoService.verificaArquivamentoGrupos();
        verify(grupoRepository, never()).save(any(Grupo.class));
        assertEquals(StatusGrupo.ARQUIVADO, grupoArquivado.getStatusGrupo());
    }
}
