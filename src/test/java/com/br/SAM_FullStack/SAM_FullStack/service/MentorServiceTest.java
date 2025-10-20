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
import java.util.Collections;
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

    //TESTE DE UNIDADE/INTEGRAÇÃO (MOCKS DE LÓGICA E FLUXO)
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Salvar Mentor deve encriptar senha, setar status e enviar email")
    void save_DeveAplicarRegrasDeNegocio() {
        // Dados de entrada com senha em texto puro e status nulo
        Mentor novoMentor = new Mentor(
                null, "Maria", "00000000000", "maria@teste.com",
                "senhaNova123", "999999999", "1 ano", null,
                TipoDeVinculo.PJ, area, null, null, "Resumo Maria", null
        );

        String senhaOriginal = novoMentor.getSenha();
        String senhaEncriptada = "$2a$10$HASHENCRIPTADO";

        //Mockando o PasswordEncoder
        when(passwordEncoder.encode(senhaOriginal)).thenReturn(senhaEncriptada);

        //Mockando o Repositório
        when(mentorRepository.save(any(Mentor.class))).thenReturn(novoMentor);

        Mentor mentorSalvo = mentorService.save(novoMentor);

        // Verificações de Unidade (Lógica de Negócio)
        // 1. O status deve ser PENDENTE
        assertEquals(StatusMentor.PENDENTE, mentorSalvo.getStatusMentor(), "O status inicial deve ser PENDENTE");
        // 2. A senha deve ter sido encriptada
        assertEquals(senhaEncriptada, mentorSalvo.getSenha(), "A senha deve estar encriptada");

        // Verificações de Mocks/Comportamento
        // 3. Verifica se o Enconder foi chamado
        verify(passwordEncoder, times(1)).encode(senhaOriginal);
        // 4. Verifica se o EmailService foi chamado
        verify(emailService, times(1)).enviarEmailComTemplate(
                eq("maria@teste.com"),
                eq("Bem-vindo(a) ao SAM - Cadastro em Análise"),
                eq("emails/boasVindasMentor"),
                anyMap()
        );
        // 5. Verifica se o Repositório foi chamado para salvar
        verify(mentorRepository, times(1)).save(novoMentor);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Update Mentor deve atualizar apenas campos preenchidos")
    void update_DeveAtualizarApenasCamposPreenchidos() {
        //Mockando o findById (busca o Mentor existente)
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        //Mockando o retorno do save
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        // Dados de atualização (apenas nome e email preenchidos)
        Mentor mentorUpdate = new Mentor();
        mentorUpdate.setNome("Novo Nome");
        mentorUpdate.setEmail("novoemail@teste.com");

        Mentor mentorAtualizado = mentorService.update(mentor.getId(), mentorUpdate);

        // Verificação:
        assertEquals("Novo Nome", mentorAtualizado.getNome(), "O nome deve ser atualizado");
        assertEquals("novoemail@teste.com", mentorAtualizado.getEmail(), "O email deve ser atualizado");

        // Verificação de Mocks
        verify(mentorRepository, times(1)).findById(mentor.getId());
        verify(mentorRepository, times(1)).save(mentor);
    }

    // Cobertura dos metodos de busca por email e area
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Busca por Email deve retornar Mentor")
    void findByEmail_DeveRetornarMentor() {
        when(mentorRepository.findByEmail(mentor.getEmail())).thenReturn(Optional.of(mentor));
        Mentor resultado = mentorService.findByEmail(mentor.getEmail());
        assertNotNull(resultado);
        verify(mentorRepository, times(1)).findByEmail(mentor.getEmail());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Busca por Email deve retornar Null quando não encontrado")
    void findByEmail_DeveRetornarNull() {
        when(mentorRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());
        Mentor resultado = mentorService.findByEmail("naoexiste@email.com");
        assertNull(resultado);
        verify(mentorRepository, times(1)).findByEmail("naoexiste@email.com");
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO – Busca por Área deve retornar lista de Mentores ATIVOS")
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
