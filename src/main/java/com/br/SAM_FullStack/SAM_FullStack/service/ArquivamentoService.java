package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusGrupo;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ArquivamentoService {

    private final GrupoRepository grupoRepository;
    private final ProjetoRepository projetoRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void verificaArquivamento(){
        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();

        List<Projeto> projetosAtivos = projetoRepository.findAll(); // Apenas para teste, deve ser substituido pelo findByStatus

        for (Projeto projeto : projetosAtivos) {
            boolean arquivar = false;

            if(projeto.getDataFinalProjeto() != null && projeto.getDataFinalProjeto().isBefore(hoje)){
                arquivar = true;
            } else if (projeto.getDataFinalProjeto() == null && (mesAtual == 8 || mesAtual == 12)) {
                arquivar = true;
            }

            if (arquivar){
               // projeto.setStatusProjeto(ARQUIVADO);
                projetoRepository.save(projeto);

                Grupo grupo = projeto.getGrupo();
                if (grupo != null && !grupo.getStatusGrupo().equals(StatusGrupo.ARQUIVADO)){
                    grupo.setStatusGrupo(StatusGrupo.ARQUIVADO);
                    grupoRepository.save(grupo);
                }
            }
        }
    }

    @Scheduled(cron = "0 10 0 * * *")
    public void verificaArquivamentoGrupos(){
        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();

        List<Grupo> gruposAtivos = grupoRepository.findByStatusGrupo(StatusGrupo.ATIVO);

        for (Grupo grupo : gruposAtivos) {
           if (grupo.getStatusGrupo().equals(StatusGrupo.ATIVO) && (mesAtual == 8 || mesAtual == 12)) {
               grupo.setStatusGrupo(StatusGrupo.ARQUIVADO);
               grupoRepository.save(grupo);
            }
        }
    }
}
