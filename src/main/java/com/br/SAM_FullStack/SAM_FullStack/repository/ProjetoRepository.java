package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository <Projeto, Long> {

    //FILTRA PROJETOS QUE CONTENHAM PARTE DO NOME
    List<Projeto> findByNomeIgnoreCaseContaining(String nome);

    //FILTRA POR AREA DE ATUAÇÃO
    List<Projeto> findByAreaAtuacao(AreaDeAtuacao areaDeAtuacao);
}
