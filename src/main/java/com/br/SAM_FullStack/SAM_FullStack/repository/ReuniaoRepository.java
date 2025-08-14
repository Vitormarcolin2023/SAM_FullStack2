package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    //Recuperar as reuniões conforme o id do grupo
    @Query("SELECT ALL from Reuniao r  WHERE r.grupo = :id")
    public List<Reuniao> findAllAluno(@Param("id") long id);

    @Query("SELECT ALL from Reuniao r WHERE r.mentor = :idMentor")
    public List<Reuniao> findAllMentor(@Param("idMentor") long id);


}
