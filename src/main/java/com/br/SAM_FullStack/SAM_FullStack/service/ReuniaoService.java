package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Reuniao;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusReuniao;
import com.br.SAM_FullStack.SAM_FullStack.repository.ReuniaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;

    // Metodo para coordenação - ver todas
    public List<Reuniao> findAll() {
        return reuniaoRepository.findAll();
    }

    // Metodo para grupo - ver só reuniões do grupo
    public List<Reuniao> findAllByGrupo(long idGrupo) {
        return reuniaoRepository.findAllAluno(idGrupo);
    }

    // Metodo para mentor - ver só reuniões do mentor
    public List<Reuniao> findAllByMentor(long idMentor) {
        return reuniaoRepository.findAllMentor(idMentor);
    }

    // Buscar por ID
    public Reuniao findById(long id) {
        return reuniaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reunião não encontrada com id " + id));
    }

    // Salvar uma reunião (nova solicitação sempre começa como PENDENTE)
    public String save(Reuniao reuniao) {
        reuniao.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniaoRepository.save(reuniao);
        return "Solicitação de reunião enviada";
    }

    // Atualizar reunião
    public String update(long id, Reuniao reuniaoAtualizada) {
        Reuniao reuniao = this.findById(id);

        if(reuniao == null){
         throw new IllegalStateException("Reunião com id " + id + " não encontrada");
        }
        reuniao.setAssunto(reuniaoAtualizada.getAssunto());
        reuniao.setData(reuniaoAtualizada.getData());
        reuniao.setHora(reuniaoAtualizada.getHora());
        reuniao.setFormatoReuniao(reuniaoAtualizada.getFormatoReuniao());
        reuniao.setStatusReuniao(reuniaoAtualizada.getStatusReuniao());
        reuniao.setGrupo(reuniaoAtualizada.getGrupo());
        reuniao.setMentor(reuniaoAtualizada.getMentor());

        reuniaoRepository.save(reuniao);
        return ("Reunião atualizada com sucesso!");
    }

    // Deletar reunião
    public String delete(long id) {
        Reuniao reuniao = this.findById(id);
        if (reuniao == null){
            throw new IllegalStateException("Reunião não encontrada");
        }
        reuniaoRepository.delete(reuniao);
        return ("Reunião deletada com sucesso");
    }
}
