package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.model.TipoDeVinculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MentorRepositoryTest {

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Mentor mentorAtivo;
    private Mentor mentorPendente;
    private AreaDeAtuacao areaTI;
    private AreaDeAtuacao areaSaude;

    @BeforeEach
    void setUp() {
        // Configura as Áreas de Atuação
        areaTI = new AreaDeAtuacao(null, "Tecnologia da Informação");
        areaSaude = new AreaDeAtuacao(null, "Saúde");

        entityManager.persist(areaTI);
        entityManager.persist(areaSaude); // Persiste a nova área
        entityManager.flush();

        // Cria Mentor Ativo (na área TI)
        mentorAtivo = new Mentor(
                null, "Alice Teste", "11111111111", "alice@ativo.com",
                "hashAtivo", "888888888", "3 anos", StatusMentor.ATIVO,
                TipoDeVinculo.CLT, areaTI, null, null, "Resumo Ativo", null
        );

        // Cria Mentor Pendente (na área TI)
        mentorPendente = new Mentor(
                null, "Bob Pendente", "22222222222", "bob@pendente.com",
                "hashPendente", "777777777", "1 ano", StatusMentor.PENDENTE,
                TipoDeVinculo.PJ, areaTI, null, null, "Resumo Pendente", null
        );

        // Salva no banco de dados em memória
        entityManager.persist(mentorAtivo);
        entityManager.persist(mentorPendente);
        entityManager.flush();
    }

    // TESTES PARA findByEmail
    @Test
    @DisplayName("REPO – Busca por Email deve encontrar Mentor existente")
    void findByEmail_DeveRetornarMentorExistente() {
        // Ação
        Optional<Mentor> resultado = mentorRepository.findByEmail(mentorAtivo.getEmail());

        // Verificação
        assertTrue(resultado.isPresent(), "O Optional deve conter o mentor");
        assertEquals(mentorAtivo.getNome(), resultado.get().getNome(), "O nome deve ser o esperado");
    }

    @Test
    @DisplayName("REPO – Busca por Email deve retornar Optional vazio quando não existir")
    void findByEmail_DeveRetornarOptionalVazio() {
        // Ação
        Optional<Mentor> resultado = mentorRepository.findByEmail("email@naoexiste.com");

        // Verificação
        assertFalse(resultado.isPresent(), "O Optional deve estar vazio");
    }

    // TESTES PARA findByEmailAndSenha
    @Test
    @DisplayName("REPO – Busca por Email e Senha deve retornar Mentor quando credenciais corretas")
    void findByEmailAndSenha_DeveRetornarMentorComCredenciaisCorretas() {
        Optional<Mentor> resultado = mentorRepository.findByEmailAndSenha(
                mentorAtivo.getEmail(), mentorAtivo.getSenha()
        );

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(mentorAtivo.getEmail(), resultado.get().getEmail());
    }

    @Test
    @DisplayName("REPO – Busca por Email e Senha deve retornar Optional vazio quando senha incorreta")
    void findByEmailAndSenha_OptionalVazioComSenhaIncorreta() {
        // Ação
        Optional<Mentor> resultado = mentorRepository.findByEmailAndSenha(
                mentorAtivo.getEmail(), "senhaIncorreta"
        );

        // Verificação
        assertFalse(resultado.isPresent());
    }

    // TESTES PARA findByAreaDeAtuacaoIdAndStatusMentor
    @Test
    @DisplayName("REPO – Busca por Area e Status deve retornar apenas Mentores ATIVOS")
    void findByAreaDeAtuacaoIdAndStatusMentor_MentoresAtivos() {
        // Ação: Busca mentores na área de TI que estejam ATIVOS
        List<Mentor> resultado = mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(
                areaTI.getId(), StatusMentor.ATIVO
        );

        // Verificação
        assertNotNull(resultado);
        assertEquals(1, resultado.size(), "Deve haver apenas um mentor ATIVO nesta área");
        assertEquals(mentorAtivo.getEmail(), resultado.get(0).getEmail());
        assertEquals(StatusMentor.ATIVO, resultado.get(0).getStatusMentor());
    }

    @Test
    @DisplayName("REPO – Busca por Area e Status deve retornar lista vazia se nenhum ativo na area")
    void findByAreaDeAtuacaoIdAndStatusMentor_ListaVazia() {
        // Ação: Busca mentores na área de Saúde (onde não persista nenhum mentor) que estejam ATIVOS
        List<Mentor> resultado = mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(
                areaSaude.getId(), StatusMentor.ATIVO
        );

        // Verificação
        assertTrue(resultado.isEmpty(), "A lista deve estar vazia, pois não há mentores ativos nesta área");
    }
}