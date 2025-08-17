package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final GrupoRepository grupoRepository;

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
         public Projeto save (Projeto projeto) {
             // 1. Verificar se o Projeto tem um Grupo associado
             if (projeto.getGrupo() != null && projeto.getGrupo().getId() != 0) {

                 // 2. Buscar a entidade 'Grupo' do banco de dados para garantir que esteja 'Managed'
                 Grupo grupoGerenciado = grupoRepository.findById(projeto.getGrupo().getId())
                         .orElseThrow(() -> new RuntimeException("Grupo não encontrado"));

                 // 3. Associar a entidade 'Grupo' gerenciada ao 'Projeto'
                 projeto.setGrupo(grupoGerenciado);
             }

             // O resto do seu código
             atualizarStatusProjeto(projeto);

             // 4. Salvar o Projeto (agora com uma entidade 'Grupo' gerenciada)
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
}