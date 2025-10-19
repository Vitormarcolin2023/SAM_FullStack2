package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.model.TipoDeVinculo;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MentorService mentorService;

    private Mentor mentor;
    private AreaDeAtuacao area;

    @BeforeEach
    void setUp() {
        area = new AreaDeAtuacao(1L, "Arquitetura");

        mentor = new Mentor(
                1L, "João Teste", "12345678900", "joao@teste.com",
                "senha123", "999999999", "5 anos", StatusMentor.ATIVO,
                TipoDeVinculo.CLT, area, null, null, "Resumo Teste", null
        );
    }

    //testes de integração -Mock Repository
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – listagem de todos os Mentores (Lista preenchida)")
    void listAll_DeveRetornarListaDeMentores() {
        Mentor mentor2 = new Mentor();
        List<Mentor> listaEsperada = Arrays.asList(mentor, mentor2);
        when(mentorRepository.findAll()).thenReturn(listaEsperada);

        //listagem
        List<Mentor> listaAtual = mentorService.listAll();

        // Verificação
        assertFalse(listaAtual.isEmpty(), "A lista não deveria estar vazia");
        assertEquals(2, listaAtual.size(), "A lista deve conter 2 mentores");
        verify(mentorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – busca de Mentor por ID")
    void findById_DeveRetornarMentorQuandoEncontrado() {
        //Mocka o Repositório para retornar o Mentor
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        Mentor mentorEncontrado = mentorService.findById(mentor.getId());

        // Verificação
        assertNotNull(mentorEncontrado);
        assertEquals(mentor.getNome(), mentorEncontrado.getNome());
        verify(mentorRepository, times(1)).findById(mentor.getId());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – lançar runtimeException  ao buscar Mentor por ID inexistente")
    void findById_MentorNaoEncontrado() {
        // Mocka o Repositório para retornar Optional vazio
        when(mentorRepository.findById(99L)).thenReturn(Optional.empty());

        // ação e verificação
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mentorService.findById(99L);
        });

        assertEquals("Mentor não encontrado", exception.getMessage());
        verify(mentorRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Delete Mentor")
    void delete_DeveDeletarMentorQuandoEncontrado() {
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        mentorService.delete(mentor.getId());

        // Verificação: Deve chamar findById e em seguida o delete do repository
        verify(mentorRepository, times(1)).findById(mentor.getId());
        verify(mentorRepository, times(1)).delete(mentor);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Lança exceção ao tentar deletar Mentor inexistente")
    void delete_ExcecaoQuandoMentorNaoEncontrado() {
        //Mockando o findById para Optional vazio
         when(mentorRepository.findById(99L)).thenReturn(Optional.empty());

        // Ação e Verificação
        assertThrows(RuntimeException.class, () -> {
            mentorService.delete(99L);
        });

        // O metodo delete do repository NÃO deve ser chamado
        verify(mentorRepository, never()).delete(any(Mentor.class));
    }
}
