package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final GrupoRepository grupoRepository;

    @Autowired
    private MentorRepository mentorRepository;

    public ProjetoService(ProjetoRepository projetoRepository, GrupoRepository grupoRepository) {
        this.projetoRepository = projetoRepository;
        this.grupoRepository = grupoRepository;
    }
        //LISTAR PROJETO
        public List<Projeto> listAll () {
            return projetoRepository.findAll();
        }
        //BUSCAR POR ID
        public Projeto findById ( long id){
            return projetoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        }

        public List<Projeto>buscarPorNome(String nomeDoProjeto){
        return projetoRepository.findByNomeDoProjetoIgnoreCaseContaining(nomeDoProjeto);
        }

        public List<Projeto>buscarPorAreaAtuacao(AreaDeAtuacao areaDeAtuacao){
        return projetoRepository.findByAreaDeAtuacao(areaDeAtuacao);
        }

        private void atualizarStatusProjeto(Projeto projeto){
            LocalDate hoje = LocalDate.now();

            if (projeto.getDataFinalProjeto().isBefore(hoje)){
                projeto.setStatusProjeto("ENCERRADO");
            }else if(projeto.getDataInicioProjeto().isAfter(hoje)){
                projeto.setStatusProjeto("NAO_INICIADO");
            }else {
                projeto.setStatusProjeto("EM_ANDAMENTO");
            }
        }

         // SALVAR
         public Projeto save(Projeto projeto) {
             // Se o projeto tiver grupo associado
             if (projeto.getGrupo() != null && projeto.getGrupo().getId() != null) {

                 Grupo grupoGerenciado = grupoRepository.findByIdWithAlunos(projeto.getGrupo().getId())
                         .orElse(projeto.getGrupo());

                 projeto.setGrupo(grupoGerenciado);


                 if (grupoGerenciado.getAlunos() != null) {
                     for (Aluno aluno : grupoGerenciado.getAlunos()) {
                         if (aluno.getCurso() != null
                                 && aluno.getCurso().getAreaDeAtuacao() != null
                                 && projeto.getAreaDeAtuacao() != null
                                 && !aluno.getCurso().getAreaDeAtuacao().getId()
                                 .equals(projeto.getAreaDeAtuacao().getId())) {
                             throw new IllegalArgumentException(
                                     "A área de atuação do projeto deve ser a mesma de todos os alunos do grupo."
                             );
                         }
                     }
                 }
             }

             atualizarStatusProjeto(projeto);

             return projetoRepository.save(projeto);
         }


    //atualizar
        public Projeto update (Long id, Projeto projetoUpdate){
            Projeto projetoExistente = findById(id);
            projetoExistente.setNomeDoProjeto(projetoUpdate.getNomeDoProjeto());
            projetoExistente.setDataInicioProjeto(projetoUpdate.getDataInicioProjeto());
            projetoExistente.setDataFinalProjeto(projetoUpdate.getDataFinalProjeto());
            projetoExistente.setDescricao(projetoUpdate.getDescricao());
            projetoExistente.setPeriodo(projetoUpdate.getPeriodo());
            projetoExistente.setAreaDeAtuacao(projetoUpdate.getAreaDeAtuacao());

            atualizarStatusProjeto(projetoExistente);

            return projetoRepository.save(projetoExistente);
        }

        public void delete(Long id){
             Projeto projeto = findById(id);
             projetoRepository.delete(projeto);
    }

    @Transactional
    public void desvincularMentor(Long mentorId) {
        List<Projeto> projetos = projetoRepository.findByMentorId(mentorId);
        for (Projeto p : projetos) {
            p.setMentor(null);
            projetoRepository.save(p);
        }
    }

    public List<Projeto> findByMentor(Long mentorId) {
        List<Projeto> projetos = projetoRepository.findByMentorId(mentorId);

        if (projetos.isEmpty()) {
            throw new RuntimeException("Nenhum projeto encontrado para este mentor.");
        }

        return projetos;
    }

    public List<Projeto> buscarProjetosPorProfessor(Long professorId) {
        return projetoRepository.findAllByProfessoresId(professorId);
    }

}