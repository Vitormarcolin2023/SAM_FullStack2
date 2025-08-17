package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByNome(String nome);

    Optional<Aluno> findByRa(Integer ra);

    Aluno findByStatusAlunoGrupo(StatusAlunoGrupo statusAlunoGrupo);

    @Query(
            "Select a from Aluno a where a.nome = :nome"
    )
    List<Aluno> getByNome(@Param("nome") String nome);

    @Query(
            "Select a from Aluno a where a.ra = :ra"
    )
    List<Aluno> getByRa(@Param("ra") Integer ra);

    // Busca uma lista de alunos cujo nome contenha um determinado texto, ignorando maiúsculas/minúsculas.
    List<Aluno> findByNomeContainingIgnoreCase(String nome);

    List<Aluno> findAllByOrderByNomeAsc();
}