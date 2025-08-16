package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoordenadorService {

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private MentorService mentorService;

    public String save(Coordenador coordenador){
        this.coordenadorRepository.save(coordenador);
        return "Coordenador salvo com sucesso!";
    }

    public String update(Coordenador coordenador, long id){
        coordenador.setId(id);
        this.coordenadorRepository.save(coordenador);
        return "Coordenador atualizado com sucesso!";
    }

    public String ativarMentor(long mentorId){
        try {
            String mensagem = this.mentorService.updateStatus(mentorId, "CONCLUIDO");
            return "Mentor ativado com sucesso!";
        } catch (Exception e) {
            return "Erro ao tentar ativar o mentor.";
        }
    }
}
