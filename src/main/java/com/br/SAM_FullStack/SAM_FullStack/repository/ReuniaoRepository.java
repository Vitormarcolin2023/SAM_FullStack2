package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    // Reuniões por grupo
    @Query("SELECT r FROM Reuniao r WHERE r.grupo.id = :idGrupo")
    List<Reuniao> findAllGrupo(@Param("idGrupo") long idGrupo);

    // Reuniões por mentor
    @Query("SELECT r FROM Reuniao r WHERE r.mentor.id = :idMentor")
    List<Reuniao> findAllMentor(@Param("idMentor") long idMentor);
}
