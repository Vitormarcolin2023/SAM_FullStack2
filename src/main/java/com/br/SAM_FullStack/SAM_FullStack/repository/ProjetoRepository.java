package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository <Projeto, Long> {

    List<Projeto> findByNomeDoProjetoIgnoreCaseContaining(String nomeDoProjeto);

    List<Projeto> findByMentorId(Long mentorId);
    List<Projeto> findByAreaDeAtuacao(AreaDeAtuacao areaDeAtuacao);
    List<Projeto> findAllByProfessoresId(Long professorId);

}
