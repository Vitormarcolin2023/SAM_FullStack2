package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReuniaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    private Grupo grupo;
    private Reuniao reuniao1;
    private Reuniao reuniao2;
    private Mentor mentor;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Date data = sdf.parse("2025-10-23");
    private Time hora = Time.valueOf("14:00:00");

    public ReuniaoRepositoryTest() throws ParseException {
    }

    @BeforeEach
    void setup(){
        AreaDeAtuacao areaDeAtuacao = new AreaDeAtuacao(null, "Tecnologia");
        entityManager.persistAndFlush(areaDeAtuacao);
        Curso curso = new Curso(null, "ADS", areaDeAtuacao);
        entityManager.persistAndFlush(curso);

        Aluno aluno1 =  new Aluno(null, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno2 = new Aluno(null, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno3  = new Aluno(null, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);
        entityManager.persistAndFlush(aluno1);
        entityManager.persistAndFlush(aluno2);
        entityManager.persistAndFlush(aluno3);

        mentor = new Mentor();
        mentor.setNome("Romana Novaes");
        mentor.setEmail("romana@teste.com");
        mentor.setSenha("senha123");
        mentor.setTelefone("11999999999");
        mentor.setCpf("12345678900");
        mentor.setTipoDeVinculo(TipoDeVinculo.CLT);
        mentor.setAreaDeAtuacao(areaDeAtuacao);

        entityManager.persistAndFlush(mentor);

        entityManager.persistAndFlush(mentor);

        grupo = new Grupo(null, "Grupo Ativo", StatusGrupo.ATIVO, aluno1, List.of(aluno1, aluno2, aluno3));
        entityManager.persistAndFlush(grupo);

        reuniao1 = new Reuniao(null, "Validar requisitos do projeto", data, hora, FormatoReuniao.ONLINE, StatusReuniao.ACEITO, mentor, grupo);
        reuniao2 = new Reuniao(null, "Assinar documentos", data, hora, FormatoReuniao.PRESENCIAL, StatusReuniao.PENDENTE, mentor, grupo);
        entityManager.persistAndFlush(reuniao1);
        entityManager.persistAndFlush(reuniao2);
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões por Grupo")
    void buscarReunioesGrupo_deveRetornarListaDeReunioes(){
        List<Reuniao> retorno = reuniaoRepository.findAllGrupo(grupo.getId());

        assertFalse(retorno.isEmpty(), "A lista de grupos não pode estar vazia");
        assertEquals("Validar requisitos do projeto", retorno.get(0).getAssunto());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existir reuniões para o grupo informado")
    void buscarReunioesGrupo_quandoNaoExistirReunioes_deveRetornarListaVazia() {
        Grupo grupoSemReuniao = new Grupo(null, "Grupo Sem Reunião", StatusGrupo.ATIVO, null, List.of());
        entityManager.persistAndFlush(grupoSemReuniao);

        List<Reuniao> retorno = reuniaoRepository.findAllGrupo(grupoSemReuniao.getId());
        assertFalse(!retorno.isEmpty(), "Era esperado que a lista tivesse reuniões, mas está vazia");
    }


    @Test
    @DisplayName("Deve retornar lista de reuniões por Mentor")
    void buscarReunioesMentor_deveRetornarListaDeReunioes(){
        List<Reuniao> retorno = reuniaoRepository.findAllMentor(mentor.getId());

        assertFalse(retorno.isEmpty(), "A lista de grupos não pode estar vazia");
        assertEquals("Validar requisitos do projeto", retorno.get(0).getAssunto());
    }
}
