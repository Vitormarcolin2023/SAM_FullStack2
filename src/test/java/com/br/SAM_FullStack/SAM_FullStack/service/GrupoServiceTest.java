package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GrupoServiceTest {

    @Autowired
    GrupoService grupoService;

    @MockitoBean
    GrupoRepository grupoRepository;

    @BeforeEach
    void setup(){
        AreaDeAtuacao areaDeAtuacao1 = new AreaDeAtuacao(1L, "Tecnologia");
        AreaDeAtuacao areaDeAtuacao2 = new AreaDeAtuacao(2L, "Saúde");

        Curso curso1 = new Curso(1L, "Engenharia de Software", areaDeAtuacao1);
        Curso curso2 = new Curso(2L, "Veterinária", areaDeAtuacao2);

        Aluno aluno1 = new Aluno();
        aluno1.setId(1L);
        aluno1.setNome("Ana Silva");
        aluno1.setRa(1001);
        aluno1.setSenha("senha123");
        aluno1.setEmail("ana.silva@email.com");
        aluno1.setCurso(curso1);
        aluno1.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        Aluno aluno2 = new Aluno();
        aluno2.setId(2L);
        aluno2.setNome("Bruno Costa");
        aluno2.setRa(1002);
        aluno2.setSenha("senha123");
        aluno2.setEmail("bruno.costa@email.com");
        aluno2.setCurso(curso1);
        aluno2.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        Aluno aluno3 = new Aluno();
        aluno3.setId(3L);
        aluno3.setNome("Carla Mendes");
        aluno3.setRa(1003);
        aluno3.setSenha("senha123");
        aluno3.setEmail("carla.mendes@email.com");
        aluno3.setCurso(curso1);
        aluno3.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        Aluno aluno4 = new Aluno();
        aluno4.setId(4L);
        aluno4.setNome("Diego Oliveira");
        aluno4.setRa(1004);
        aluno4.setSenha("senha123");
        aluno4.setEmail("diego.oliveira@email.com");
        aluno4.setCurso(curso1);
        aluno4.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        Aluno aluno5 = new Aluno();
        aluno5.setId(5L);
        aluno5.setNome("Elisa Fernandes");
        aluno5.setRa(1005);
        aluno5.setSenha("senha123");
        aluno5.setEmail("elisa.fernandes@email.com");
        aluno5.setCurso(curso2);
        aluno5.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        Aluno aluno6 = new Aluno();
        aluno6.setId(6L);
        aluno6.setNome("Fábio Santos");
        aluno6.setRa(1006);
        aluno6.setSenha("senha123");
        aluno6.setEmail("fabio.santos@email.com");
        aluno6.setCurso(curso2);
        aluno6.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        Aluno aluno7 = new Aluno();
        aluno7.setId(7L);
        aluno7.setNome("Gabriela Lima");
        aluno7.setRa(1007);
        aluno7.setSenha("senha123");
        aluno7.setEmail("gabriela.lima@email.com");
        aluno7.setCurso(curso2);
        aluno7.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        List<Aluno> alunosGrupo1 = List.of(aluno1, aluno2, aluno3, aluno4);
        List<Aluno> alunosGrupo2 = List.of(aluno5, aluno6, aluno7);

        Grupo grupo1 = new Grupo(1L, "Grupo Eng. Soft.", StatusGrupo.ATIVO, aluno1, alunosGrupo1);
        Grupo grupo2 = new Grupo(2L, "Grupo Veterinária", StatusGrupo.ATIVO, aluno5, alunosGrupo2);

        List<Grupo> grupos = List.of(grupo2, grupo1);
        List<Grupo> gruposAluno1 = List.of(grupo1);

        when(this.grupoRepository.findAll()).thenReturn(grupos);
        when(this.grupoRepository.findById(2L)).thenReturn(Optional.of(grupo2));
        when(this.grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, aluno1.getId())).thenReturn(gruposAluno1);
    }

    @Test
    @DisplayName("Deve retornar uma lista com todos os grupos salvos")
    void buscar_deveRetornarTodosOsGrupos (){
        List<Grupo> response = grupoService.findAll();
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Deve retornar um objeto grupo com o id que foi passado como parâmetro")
    void buscarPorId_quandoExiste_deveRetornarGrupo(){
        Grupo response = grupoService.findById(2L);
        assertEquals(2L, response.getId());
        assertEquals(3, response.getAlunos().size());
    }

    @Test
    @DisplayName("Deve retornar uma exceção quando o id passado não existir")
    void buscarPorId_quandoNaoExiste_deveCairEmException(){
        assertThrows(Exception.class, () -> {
            Grupo response = grupoService.findById(-4L);
        });
    }

    @Test
    @DisplayName("Deve retornar um grupo que o aluno participa, com base no aluno passado como parâmetro - um grupo porque o aluno pode ter apenas um grupo ativo")
    void buscarPorAlunoId_quandoExiste_deveRetornarGrupoAtivoDoAluno(){
        AreaDeAtuacao areaDeAtuacao1 = new AreaDeAtuacao(1L, "Tecnologia");
        Curso curso1 = new Curso(1L, "Engenharia de Software", areaDeAtuacao1);

        Aluno aluno1 = new Aluno();
        aluno1.setId(1L);
        aluno1.setNome("Ana Silva");
        aluno1.setRa(1001);
        aluno1.setSenha("senha123");
        aluno1.setEmail("ana.silva@email.com");
        aluno1.setCurso(curso1);
        aluno1.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);

        Grupo response = grupoService.findByAluno(aluno1);
        assertEquals(true, response.getAlunos().contains(aluno1));
        assertEquals(StatusGrupo.ATIVO, response.getStatusGrupo());
    }
}
