package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository <Projeto, Long> {

    List<Projeto> findByNomeIgnoreCaseContaining(String nome);


    List<Projeto> findByAreaAtuacao(AreaDeAtuacao areaDeAtuacao);
}
