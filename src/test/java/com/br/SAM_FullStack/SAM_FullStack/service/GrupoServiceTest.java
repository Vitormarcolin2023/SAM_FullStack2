package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GrupoServiceTest {

    @Autowired
    private GrupoService grupoService;

    @MockitoBean
    private GrupoRepository grupoRepository;
    @MockitoBean
    private AlunoRepository alunoRepository;

    private Aluno aluno1;
    private Aluno aluno5;
    private Grupo grupo1;
    private Grupo grupo2;
    private Grupo grupo3;
    private GrupoDTO grupoSalvarSucesso;

    @BeforeEach
    void setup() {
        AreaDeAtuacao area1 = new AreaDeAtuacao(1L, "Tecnologia");
        AreaDeAtuacao area2 = new AreaDeAtuacao(2L, "Saúde");

        Curso curso1 = new Curso(1L, "Engenharia de Software", area1);
        Curso curso2 = new Curso(2L, "Veterinária", area2);

        aluno1 = new Aluno(1L, "Ana Silva", 1001, "senha123", "ana.silva@email.com", curso1, StatusAlunoGrupo.ATIVO);
        Aluno aluno2 = new Aluno(2L, "Bruno Costa", 1002, "senha123", "bruno.costa@email.com", curso1, StatusAlunoGrupo.ATIVO);
        Aluno aluno3 = new Aluno(3L, "Carla Mendes", 1003, "senha123", "carla.mendes@email.com", curso1, StatusAlunoGrupo.ATIVO);
        Aluno aluno4 = new Aluno(4L, "Diego Oliveira", 1004, "senha123", "diego.oliveira@email.com", curso1, StatusAlunoGrupo.ATIVO);

        aluno5 = new Aluno(5L, "Elisa Fernandes", 1005, "senha123", "elisa.fernandes@email.com", curso2, StatusAlunoGrupo.ATIVO);
        Aluno aluno6 = new Aluno(6L, "Fábio Santos", 1006, "senha123", "fabio.santos@email.com", curso2, StatusAlunoGrupo.ATIVO);
        Aluno aluno7 = new Aluno(7L, "Gabriela Lima", 1007, "senha123", "gabriela.lima@email.com", curso2, StatusAlunoGrupo.AGUARDANDO);
        Aluno aluno8 = new Aluno(8L, "Regina Lima", 1008, "senha123", "regina.lima@email.com", curso2, StatusAlunoGrupo.AGUARDANDO);

        grupo1 = new Grupo(1L, "Grupo Eng. Soft.", StatusGrupo.ATIVO, aluno1, List.of(aluno1, aluno2, aluno3, aluno4));
        grupo3 = new Grupo(3L, "Grupo Eng. Soft Arquivado", StatusGrupo.ARQUIVADO, aluno1, List.of(aluno1, aluno2, aluno3, aluno4));
        grupo2 = new Grupo(2L, "Grupo Veterinária", StatusGrupo.ARQUIVADO, aluno5, List.of(aluno5, aluno6, aluno7));

        aluno1.setGrupos(List.of(grupo1, grupo3));
        aluno2.setGrupos(List.of(grupo1, grupo3));
        aluno3.setGrupos(List.of(grupo1, grupo3));
        aluno4.setGrupos(List.of(grupo1, grupo3));

        aluno5.setGrupos(new ArrayList<>(List.of(grupo2)));
        aluno6.setGrupos(new ArrayList<>(List.of(grupo2)));
        aluno7.setGrupos(new ArrayList<>(List.of(grupo2)));
        aluno8.setGrupos(List.of(grupo1));

        grupoSalvarSucesso = new GrupoDTO(10L, "Grupo salvo com sucesso", 5L, List.of(5L, 6L, 7L));

        List<Grupo> todosGrupos = List.of(grupo1, grupo2, grupo3);
        List<Grupo> gruposAluno1 = List.of(grupo1);

        when(grupoRepository.findAll()).thenReturn(todosGrupos);
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(grupo2));
        when(grupoRepository.findById(-1L)).thenReturn(Optional.empty());
        when(grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, aluno1.getId())).thenReturn(gruposAluno1);
        when(alunoRepository.findById(-41L)).thenReturn(Optional.empty());
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno1));
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(alunoRepository.findById(6L)).thenReturn(Optional.of(aluno6));
        when(alunoRepository.findAllById(List.of(1L, 2L, 3L, 4L))).thenReturn(List.of(aluno1, aluno2, aluno3, aluno4));
        when(alunoRepository.findAllById(List.of(5L, 6L))).thenReturn(List.of(aluno5, aluno6));
        when(alunoRepository.findAllById(List.of(6L, 7L, 8L))).thenReturn(List.of(aluno6, aluno7, aluno8));
        when(alunoRepository.findAllById(List.of(5L, 6L, 7L))).thenReturn(List.of(aluno5, aluno6, aluno7));
        when(grupoRepository.save(any(Grupo.class))).thenAnswer(invocation -> {
            Grupo grupo = invocation.getArgument(0);
            grupo.setId(10L);
            return grupo;
        });
    }

    @Test
    @DisplayName("Deve retornar todos os grupos")
    void buscar_deveRetornarTodosOsGrupos() {
        List<Grupo> response = grupoService.findAll();
        assertEquals(3, response.size());
    }

    @Test
    @DisplayName("Deve retornar o grupo pelo ID")
    void buscarPorId_quandoExiste_deveRetornarGrupo() {
        Grupo response = grupoService.findById(1L);
        assertEquals(1L, response.getId());
        assertEquals(4, response.getAlunos().size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID não existir")
    void buscarPorId_quandoNaoExiste_deveCairEmException() {
        assertThrows(RuntimeException.class, () -> grupoService.findById(-1L));
    }

    @Test
    @DisplayName("Deve retornar grupo ativo de um aluno")
    void buscarPorAlunoId_quandoExiste_deveRetornarGrupoAtivoDoAluno() {
        Grupo response = grupoService.findByAluno(aluno1);
        assertEquals(StatusGrupo.ATIVO, response.getStatusGrupo());
        assertEquals(true, response.getAlunos().contains(aluno1));
    }


    // --- cenários de erros para salvar grupo
    @Test
    @DisplayName("Deve retornar erro ao buscar id de aluno que não existe ao tentar salvar grupo")
    void salvarGrupo_quandoIdDeAlunoNaoExiste_deveRetornarException(){
        assertThrows(Exception.class, () -> {
            GrupoDTO grupoSave = new GrupoDTO(4L, "Grupo Teste Save", -41L, List.of(5L, 6L, 7L));
            grupoService.save(grupoSave);
        });
    }

    @Test
    @DisplayName("Deve retornor erro ao tentar salvar um novo grupo quando o admin informado já participa de outro grupo ativo")
    void salvarGrupo_quandoAdminJaPossuiGrupoAtivo_deveRetornarException(){
        assertThrows(IllegalStateException.class, () -> {
            GrupoDTO grupoErroStatus = new GrupoDTO(5L, "Grupo erro status", 1L, List.of(1L, 2L, 3L, 4L));
            grupoService.save(grupoErroStatus);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar novo grupo quando algum aluno informado não existe")
    void salvarGrupo_quandoAlunoInformadoNaoExiste_deveRetornarExceprion(){
        assertThrows(IllegalArgumentException.class, () -> {
            GrupoDTO grupoErroAlunos = new GrupoDTO(6L, "Grupo teste erro Alunos", 5L, List.of(5L, 6L, -7L));
            grupoService.save(grupoErroAlunos);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar grupo com qtd de alunos menor do que a permitida")
    void salvarGrupo_quandoQtdDeAlunoEhMenorDoQuePermitida_deveRetornarErro(){
        assertThrows(IllegalStateException.class, () -> {
           GrupoDTO grupoErroQtdAlunos = new GrupoDTO(7L, "Grupo teste erro qtd alunos", 5L, List.of(5L, 6L));
           grupoService.save(grupoErroQtdAlunos);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar grupo quando o id do admin não é passado cna lista de alunos")
    void salvarGrupo_quandoIdDoAdminNaoForInformadoNaLista_deveRetornarErro(){
        assertThrows(IllegalStateException.class, () -> {
           GrupoDTO grupoErroListaALunos = new GrupoDTO(8L, "Grupo teste erro lista alunos", 5L, List.of(6L, 7L, 8L));
           grupoService.save(grupoErroListaALunos);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar grupo quando algum aluno informado já participa de outro grupo ativo")
    void salvarGrupo_quandoAlgumAlunoTemGrupoAtivo_deveRetorarErro(){
        assertThrows(IllegalStateException.class, () ->{
           GrupoDTO grupoErroAlunoComGrupoAtivo = new GrupoDTO(9L, "Grupo teste erro aluno com grupo ativo",
                   6L, List.of(6L, 7L, 8L));
           grupoService.save(grupoErroAlunoComGrupoAtivo);
        });
    }

    // --- cenários de sucesso para salvar grupo

    @Test
    @DisplayName("Deve salvar novo grupo quando não há erros")
    void salvarGrupo_quandoInformacoesCorretas_deveSalvar(){
        GrupoDTO response = grupoService.save(grupoSalvarSucesso);
        assertEquals(10L, response.id());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar buscar grupo por Id que não existe")
    void updateGrupo_quandoIdErrado_deveRetornarErro(){
        assertThrows(IllegalArgumentException.class, () -> {
           this.grupoService.updateGrupoInfo(-1L, 1L, new GrupoUpdateDTO("Grupo Novo Nome"));
        });
    }


}
