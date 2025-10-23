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

// Anotação essencial para testes de Repositório -inicia apenas a camada JPA em memória
@DataJpaTest
public class MentorRepositoryTest {

    @Autowired
    private MentorRepository mentorRepository;

    // Ajuda a persistir dados de teste diretamente no banco de dados em memória
    @Autowired
    private TestEntityManager entityManager;

    private Mentor mentorAtivo;
    private Mentor mentorPendente;
    private AreaDeAtuacao areaTI;
    private AreaDeAtuacao areaSaude; // Área solicitada

    @BeforeEach
    void setUp() {
        // 1. Configura as Áreas de Atuação
        areaTI = new AreaDeAtuacao(null, "Tecnologia da Informação");
        areaSaude = new AreaDeAtuacao(null, "Saúde");

        entityManager.persist(areaTI);
        entityManager.persist(areaSaude);
        entityManager.flush(); // Garante que os IDs são gerados

        // 2. Cria Mentor Ativo (na área TI)
        // A senha é salva como a hash literal para testar findByEmailAndSenha.
        mentorAtivo = new Mentor(
                null, "Alice Teste", "11111111111", "alice@ativo.com",
                "hashAtivo", "888888888", "3 anos", StatusMentor.ATIVO,
                TipoDeVinculo.CLT, areaTI, null, null, "Resumo Ativo", null
        );

        // 3. Cria Mentor Pendente (na área TI)
        mentorPendente = new Mentor(
                null, "Bob Pendente", "22222222222", "bob@pendente.com",
                "hashPendente", "777777777", "1 ano", StatusMentor.PENDENTE,
                TipoDeVinculo.PJ, areaTI, null, null, "Resumo Pendente", null
        );

        // 4. Salva no banco de dados em memória
        entityManager.persist(mentorAtivo);
        entityManager.persist(mentorPendente);
        entityManager.flush();
    }

     //TESTES PARA findByEmail
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO REPO – Busca por Email deve encontrar Mentor existente")
    void findByEmail_DeveRetornarMentorExistente() {
        // Ação
        Optional<Mentor> resultado = mentorRepository.findByEmail(mentorAtivo.getEmail());

        // Verificação (assertTrue e assertEquals)
        assertTrue(resultado.isPresent(), "O Optional deve conter o mentor");
        assertEquals(mentorAtivo.getNome(), resultado.get().getNome(), "O nome deve ser o esperado");
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO REPO – Busca por Email deve retornar Optional vazio quando não existir")
    void findByEmail_DeveRetornarOptionalVazio() {
        // Ação
        Optional<Mentor> resultado = mentorRepository.findByEmail("email@naoexiste.com");

        // Verificação (assertFalse)
        assertFalse(resultado.isPresent(), "O Optional deve estar vazio");
    }

     //TESTES PARA findByEmailAndSenha
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO REPO – Busca por Email e Senha deve retornar Mentor quando credenciais corretas")
    void findByEmailAndSenha_DeveRetornarMentorComCredenciaisCorretas() {
        // Ação: Busca pela hash salva no setUp
        Optional<Mentor> resultado = mentorRepository.findByEmailAndSenha(
                mentorAtivo.getEmail(), mentorAtivo.getSenha()
        );

        // Verificação (assertTrue e assertEquals)
        assertTrue(resultado.isPresent());
        assertEquals(mentorAtivo.getEmail(), resultado.get().getEmail());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO REPO – Busca por Email e Senha deve retornar Optional vazio quando senha incorreta")
    void findByEmailAndSenha_OptionalVazioComSenhaIncorreta() {
        // Ação
        Optional<Mentor> resultado = mentorRepository.findByEmailAndSenha(
                mentorAtivo.getEmail(), "senhaIncorreta"
        );

        // Verificação (assertFalse)
        assertFalse(resultado.isPresent());
    }

     //TESTES PARA findByAreaDeAtuacaoIdAndStatusMentor
    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO REPO – Busca por Area e Status deve retornar apenas Mentores ATIVOS")
    void findByAreaDeAtuacaoIdAndStatusMentor_MentoresAtivos() {
        // Ação: Busca mentores na área de TI que estejam ATIVOS
        List<Mentor> resultado = mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(
                areaTI.getId(), StatusMentor.ATIVO
        );

        // Verificação (assertNotNull, assertEquals)
        assertNotNull(resultado);
        assertEquals(1, resultado.size(), "Deve haver apenas um mentor ATIVO nesta área");
        assertEquals(mentorAtivo.getEmail(), resultado.get(0).getEmail());
        assertEquals(StatusMentor.ATIVO, resultado.get(0).getStatusMentor());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO REPO – Busca por Area e Status deve retornar lista vazia se nenhum ativo na area")
    void findByAreaDeAtuacaoIdAndStatusMentor_ListaVazia() {
        // Ação: Busca mentores na área de Saúde (onde não persistimos mentores) que estejam ATIVOS
        List<Mentor> resultado = mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(
                areaSaude.getId(), StatusMentor.ATIVO
        );

        // Verificação (assertTrue)
        assertTrue(resultado.isEmpty(), "A lista deve estar vazia, pois não há mentores ativos nesta área");
    }
}