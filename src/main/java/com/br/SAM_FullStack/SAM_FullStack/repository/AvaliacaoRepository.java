package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Avaliacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByProjetoId(Long projetoId);

    boolean existsByProjetoIdAndAlunoId(Long projetoId, Long alunoId);

}