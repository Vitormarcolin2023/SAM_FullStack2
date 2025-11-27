package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository <Projeto, Long> {

    List<Projeto> findByNomeDoProjetoIgnoreCaseContaining(String nomeDoProjeto);
    List<Projeto> findByPeriodo(String periodo);

    List<Projeto> findByMentorId(Long mentorId);
    List<Projeto> findByAreaDeAtuacao(AreaDeAtuacao areaDeAtuacao);
    List<Projeto> findAllByProfessoresId(Long professorId);
    List<Projeto> findAllByMentorIdAndStatusProjeto(Long professorId, StatusProjeto statusProjeto);

    @Query("SELECT p FROM Projeto p JOIN p.grupo g JOIN g.alunos a " +
            "WHERE a.id = :alunoId AND p.statusProjeto = :status")
    Optional<Projeto> findProjetoPorStatusAluno(@Param("alunoId") Long alunoId,
                                                @Param("status") StatusProjeto status);
    List<Projeto> findByGrupo_Id(Long grupoId);

    List<Projeto> findByGrupo_IdAndStatusProjetoIn(Long grupoId, List<StatusProjeto> status);

    // Projetos de um mentor
    List<Projeto> findByMentor_Id(Long mentorId);
    List<Projeto> findByMentor_IdAndStatusProjetoIn(Long mentorId, List<StatusProjeto> status);

    // Projetos de professor (ManyToMany)
    @Query("SELECT p FROM Projeto p JOIN p.professores prof WHERE prof.id = :professorId")
    List<Projeto> findByProfessor_Id(@Param("professorId") Long professorId);
}


