package com.br.SAM_FullStack.SAM_FullStack.repository;


import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CursoRepositoryTest {
    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AreaDeAtuacaoRepository areaDeAtuacaoRepository;

    private AreaDeAtuacao area;
    private Curso cursoJavaBasico;
    private Curso cursoJavaAvancado;
    private Curso cursoPython;

    @BeforeEach
    void setup () {
        area = new AreaDeAtuacao(null, "Tecnologia");
        area = areaDeAtuacaoRepository.save(area);

        cursoJavaBasico = new Curso(null, "Java Básico", area);
        cursoJavaAvancado = new Curso(null, "Java Avançado", area);
        cursoPython = new Curso(null, "Python", area);

        cursoRepository.saveAll(List.of(cursoJavaBasico, cursoJavaAvancado, cursoPython));

    }

    @Test
    @DisplayName("Deve retorna lista de cursos que contenham 'Java' no nome (ignorando maiúsculas/minúsculas)")

    void findByNomeContainingIgnoreCase_quandoCursoExiste_deveRetornarListaDeCursos() {
      List<Curso> retorno = cursoRepository.findByNomeContainingIgnoreCase("Java");

        assertNotNull(retorno, "O retorno não deve ser nulo");
        assertFalse(retorno.isEmpty(), "A lista não deve estar vazia");
        assertEquals(2, retorno.size(), "Deve retornar exatamente 2 cursos");

        assertTrue(retorno.stream().allMatch(c -> c.getNome().toLowerCase().contains("java")),
                "Todos os cursos retornados devem conter 'Java' no nome");

    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum curso encontrado para o nome pesquiso")

    void findByNomeContainingIgnoreCase_quandoNenhumCursoEncontrado_deveRetornarListaVazia() {
        List<Curso> retorno = cursoRepository.findByNomeContainingIgnoreCase("HTML");

        assertNotNull(retorno, "O retorno não deve ser nulo");
        assertTrue(retorno.isEmpty(), "A lista deve estar vazia");
    }
    @Test
    @DisplayName("Deve retornar lista de cursos da área Tecnoligia")
    void findByAreaDeAtuacaoNomeContainingIgnoreCase_quandoAreaExiste_deveRetornarCursos() {
        List<Curso> retorno = cursoRepository.findByAreaDeAtuacaoNomeContainingIgnoreCase("tecnologia");
        assertNotNull(retorno, "O retorno não deve ser nulo");
        assertFalse(retorno.isEmpty(), "A lista não deve estar vazia");
        assertEquals(3, retorno.size(), "Deve retornar exatamente 3 cursos da área Tecnologia");
        assertTrue(retorno.stream().allMatch(c -> c.getAreaDeAtuacao().getNome().equalsIgnoreCase("Tecnologia")),
                "Todos os cursos retornados devem pertencer à área Tecnologia");
    }

}
