package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

}