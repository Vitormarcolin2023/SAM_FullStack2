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
<<<<<<< HEAD
import org.mockito.MockitoAnnotations;
=======
>>>>>>> 0506d52ae2a794340e9a668228437e9dff5e3318
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
<<<<<<< HEAD
=======
import java.util.Collections;
>>>>>>> 0506d52ae2a794340e9a668228437e9dff5e3318
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

<<<<<<< HEAD
@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

=======
// Usa a extensão do Mockito para inicializar mocks
@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {

    // Dependências mockadas
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    // A classe sob teste, com mocks injetados
>>>>>>> 0506d52ae2a794340e9a668228437e9dff5e3318
    @InjectMocks
    private MentorService mentorService;

    private Mentor mentor;
    private AreaDeAtuacao area;

    @BeforeEach
    void setUp() {
<<<<<<< HEAD

    }
}
=======
        area = new AreaDeAtuacao(1L, "Arquitetura");

        // Assumindo que o ID é Long (Wrapper), se for 'long' primitivo, mude o 1L para 0L ao criar
        mentor = new Mentor(
                1L, "João Teste", "12345678900", "joao@teste.com",
                "senha123", "999999999", "5 anos", StatusMentor.ATIVO,
                TipoDeVinculo.CLT, area, null, null, "Resumo Teste", null
        );
    }

     //TESTES DE INTEGRAÇÃO (MOCK REPOSITORY) - CRUD e Exceções
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – listagem de todos os Mentores (Lista preenchida)")
    void listAll_DeveRetornarListaDeMentores() {
        // Arrange
        Mentor mentor2 = new Mentor();
        List<Mentor> listaEsperada = Arrays.asList(mentor, mentor2);
        when(mentorRepository.findAll()).thenReturn(listaEsperada);

        // Act
        List<Mentor> listaAtual = mentorService.listAll();

        // Assert com assertEquals
        assertFalse(listaAtual.isEmpty(), "A lista não deveria estar vazia");
        assertEquals(2, listaAtual.size(), "A lista deve conter 2 mentores");
        verify(mentorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – busca de Mentor por ID")
    void findById_DeveRetornarMentorQuandoEncontrado() {
        // Arrange
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        // Act
        Mentor mentorEncontrado = mentorService.findById(mentor.getId());

        // Assert com assertEquals
        assertNotNull(mentorEncontrado);
        assertEquals(mentor.getNome(), mentorEncontrado.getNome());
        verify(mentorRepository, times(1)).findById(mentor.getId());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Cenário que lança exceção (assertThrows) ao buscar Mentor por ID inexistente")
    void findById_MentorNaoEncontrado_DeveLancarExcecao() {
        // Arrange: Retorna Optional vazio
        when(mentorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert com assertThrows
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mentorService.findById(99L);
        });

        assertEquals("Mentor não encontrado", exception.getMessage());
        verify(mentorRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Delete Mentor com sucesso")
    void delete_DeveDeletarMentorQuandoEncontrado() {
        // Arrange
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        // Act
        mentorService.delete(mentor.getId());

        // Verify
        verify(mentorRepository, times(1)).findById(mentor.getId());
        verify(mentorRepository, times(1)).delete(mentor);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Cenário que lança exceção (assertThrows) ao tentar deletar Mentor inexistente")
    void delete_ExcecaoQuandoMentorNaoEncontrado() {
        // Arrange
        when(mentorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert com assertThrows
        assertThrows(RuntimeException.class, () -> {
            mentorService.delete(99L);
        });

        // Verify: O delete do repository NÃO deve ser chamado
        verify(mentorRepository, never()).delete(any(Mentor.class));
    }

    //TESTE DE UNIDADE/INTEGRAÇÃO (LÓGICA DE NEGÓCIO)
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Salvar Mentor deve setar status PENDENTE, encriptar senha e enviar email")
    void save_DeveAplicarRegrasDeNegocio_ComSuccesso() {
        // Arrange
        Mentor novoMentor = new Mentor(
                0L, "Maria", "00000000000", "maria@teste.com",
                "senhaNova123", "999999999", "1 ano", null, // Status nulo inicial
                TipoDeVinculo.PJ, area, null, null, "Resumo Maria", null
        );

        String senhaOriginal = novoMentor.getSenha();
        String senhaEncriptada = "$2a$10$HASHENCRIPTADO";

        when(passwordEncoder.encode(senhaOriginal)).thenReturn(senhaEncriptada);
        when(mentorRepository.save(any(Mentor.class))).thenReturn(novoMentor);

        // Act
        Mentor mentorSalvo = mentorService.save(novoMentor);

        // Assert com assertEquals
        assertEquals(StatusMentor.PENDENTE, mentorSalvo.getStatusMentor(), "O status inicial deve ser PENDENTE");
        assertEquals(senhaEncriptada, mentorSalvo.getSenha(), "A senha deve estar encriptada");

        // Verify (Mocks de Comportamento)
        verify(passwordEncoder, times(1)).encode(senhaOriginal);
        verify(emailService, times(1)).enviarEmailComTemplate(eq("maria@teste.com"), anyString(), anyString(), anyMap());
        verify(mentorRepository, times(1)).save(novoMentor);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Update Mentor deve atualizar apenas campos preenchidos")
    void update_DeveAtualizarApenasCamposPreenchidos() {
        // Arrange
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        // Dados de atualização (apenas nome e email)
        Mentor mentorUpdate = new Mentor();
        mentorUpdate.setNome("Novo Nome");
        mentorUpdate.setEmail("novoemail@teste.com");

        // Act
        Mentor mentorAtualizado = mentorService.update(mentor.getId(), mentorUpdate);

        // Assert com assertEquals
        assertEquals("Novo Nome", mentorAtualizado.getNome(), "O nome deve ser atualizado");
        assertEquals("novoemail@teste.com", mentorAtualizado.getEmail(), "O email deve ser atualizado");

        // Garante que campos nulos na atualização não mudam o original
        assertNotNull(mentorAtualizado.getCpf());

        // Verify
        verify(mentorRepository, times(1)).findById(mentor.getId());
        verify(mentorRepository, times(1)).save(mentor);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Update Status (updateStatus) deve mudar o status e salvar")
    void updateStatus_DeveMudarStatusDoMentor_ComSucesso() {
        // Arrange
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        //Mudar para INATIVO
        String resultado = mentorService.updateStatus(mentor.getId(), "INATIVO");

        // Assert com assertEquals
        assertEquals("Status do mentor atualizado com sucesso!", resultado);
        assertEquals(StatusMentor.INATIVO, mentor.getStatusMentor(), "O status deve ser INATIVO");

        // Verify
        verify(mentorRepository, times(1)).findById(mentor.getId());
        verify(mentorRepository, times(1)).save(mentor);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Update Status (assertThrows) para ID inexistente")
    void updateStatus_DeveLancarExcecaoQuandoMentorNaoEncontrado() {
        // Arrange
        when(mentorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert com assertThrows
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mentorService.updateStatus(99L, "ATIVO");
        });

        assertEquals("Mentor não encontrado.", exception.getMessage());
        verify(mentorRepository, times(1)).findById(99L);
        verify(mentorRepository, never()).save(any(Mentor.class));
    }

     //TESTES DE CONSULTA CUSTOMIZADA
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Busca por Email deve retornar Mentor")
    void findByEmail_DeveRetornarMentor() {
        when(mentorRepository.findByEmail(mentor.getEmail())).thenReturn(Optional.of(mentor));
        Mentor resultado = mentorService.findByEmail(mentor.getEmail());
        assertNotNull(resultado);
        verify(mentorRepository, times(1)).findByEmail(mentor.getEmail());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Busca por Email deve retornar Null (assertEquals) quando não encontrado")
    void findByEmail_DeveRetornarNull() {
        when(mentorRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());
        Mentor resultado = mentorService.findByEmail("naoexiste@email.com");
        assertNull(resultado);
        verify(mentorRepository, times(1)).findByEmail("naoexiste@email.com");
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Busca por Área deve retornar lista (assertEquals) de Mentores ATIVOS")
    void findByArea_DeveRetornarListaFiltrada() {
        List<Mentor> listaAtivos = Collections.singletonList(mentor);
        when(mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(area.getId(), StatusMentor.ATIVO))
                .thenReturn(listaAtivos);

        List<Mentor> resultado = mentorService.findByArea(area.getId());

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(mentorRepository, times(1)).findByAreaDeAtuacaoIdAndStatusMentor(area.getId(), StatusMentor.ATIVO);
    }
}
>>>>>>> 0506d52ae2a794340e9a668228437e9dff5e3318
