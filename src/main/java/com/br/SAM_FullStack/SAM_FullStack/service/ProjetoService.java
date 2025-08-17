package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;

    public ProjetoService(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
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

        public List<Projeto>buscarPorNome(String nome){
        return projetoRepository.findByNomeIgnoreCaseContaining(nome);
        }

        public List<Projeto>buscarPorAreaAtuacao(AreaDeAtuacao areaDeAtuacao){
        return projetoRepository.findByAreaAtuacao(areaDeAtuacao);
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
         public Projeto save (Projeto projeto){
            if (projeto.getTamanhoDoGrupo() < 2 || projeto.getTamanhoDoGrupo() >6){
            throw new RuntimeException("Tamanho do grupo deve ser no minimo 2 alunos e no maximo 6.");
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
            projetoExistente.setTamanhoDoGrupo(projetoUpdate.getTamanhoDoGrupo());
            if (projetoUpdate.getTamanhoDoGrupo() < 2 || projetoUpdate.getTamanhoDoGrupo() >6){
                throw new RuntimeException("Tamanho do grupo deve ser no minimo 2 alunos e no maximo 6.");
            }

            atualizarStatusProjeto(projetoExistente);

            return projetoRepository.save(projetoExistente);
        }

        public void delete(Long id){
             Projeto projeto = findById(id);
             projetoRepository.delete(projeto);
    }
}