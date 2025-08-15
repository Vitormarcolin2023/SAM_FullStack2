package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Reuniao;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusReuniao;
import com.br.SAM_FullStack.SAM_FullStack.repository.ReuniaoRepository;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@NoArgsConstructor
public class ReuniaoService {

    private ReuniaoRepository reuniaoRepository;

    // Método para ser usado por coordenação - para verificar TODAS as reuniões
    public List<Reuniao> findAll(){
        return reuniaoRepository.findAll();
    }

    //Método para ser usado por GRUPO para filtrar APENAS as reuniões que o grupo dele participa
    public List<Reuniao> findAllAluno(long id){
        return reuniaoRepository.findAllAluno(id);
    }

    // Método para ser usado por MENTORES para filtrar APENAS as reuniões que o mentor participa
    public List<Reuniao> findAllMentor(long idMentor){
        return reuniaoRepository.findAllMentor(idMentor);
    }

    // Método para salvar uma reuniao requisitada
    public String save (Reuniao reuniao){
        reuniao.setStatusReuniao(StatusReuniao.PENDENTE);
        this.reuniaoRepository.save(reuniao);
        return ("Solicitação de reunião enviada");
    }
}
