package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ReuniaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReuniaoService1 {

    @Autowired
    private final ReuniaoRepository reuniaoRepository;
    private final GrupoRepository grupoRepository;
    private final MentorRepository mentorRepository;
    private final AlunoRepository alunoRepository;

    // Metodo para coordenação - ver todas
    public List<Reuniao> findAll() {
        return reuniaoRepository.findAll();
    }

    // Metodo para grupo - ver só reuniões do grupo
    public List<Reuniao> findAllByGrupo(long idGrupo) {
        return reuniaoRepository.findAllGrupo(idGrupo);
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

    public String save(ReuniaoDTO reuniaoDTO) {
        Grupo grupo = grupoRepository.findById(reuniaoDTO.getGrupoId())
                .orElseThrow(() -> new IllegalStateException("Grupo não encontrado"));
        Mentor mentor = mentorRepository.findById(reuniaoDTO.getMentorId())
                .orElseThrow(() -> new IllegalStateException("Mentor não encontrado"));

        // Verifique se a área de atuação do mentor é a mesma de todos os alunos do grupo.
        AreaDeAtuacao areaMentor = mentor.getAreaDeAtuacao();
        List<Aluno> alunosDoGrupo = alunoRepository.findAllByGrupoId(grupo.getId());

        for (Aluno aluno : alunosDoGrupo) {
            if (aluno.getCurso().getAreaDeAtuacao() == null || !aluno.getCurso().getAreaDeAtuacao().equals(areaMentor)) {
                throw new IllegalStateException("A área de atuação de pelo menos um aluno não corresponde à do mentor.");
            }
        }
        Reuniao reuniao = new Reuniao();
        reuniao.setAssunto(reuniaoDTO.getAssunto());
        reuniao.setData(reuniaoDTO.getData());
        reuniao.setHora(reuniaoDTO.getHora());
        reuniao.setFormatoReuniao(reuniaoDTO.getFormatoReuniao());
        reuniao.setGrupo(grupo);
        reuniao.setMentor(mentor);
        reuniao.setStatusReuniao(StatusReuniao.PENDENTE);

        reuniaoRepository.save(reuniao);
        return "Solicitação de reunião enviada";
    }

    public String update(long id, Reuniao reuniaoAtualizada) {

        Reuniao reuniaoexiste = reuniaoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reunião não encontrada"));

        if(!reuniaoexiste.getStatusReuniao().equals(StatusReuniao.PENDENTE)){
            throw new IllegalStateException("Operação não permitida. A reunião já foi avaliada pelo solicitado");
        }

        if (reuniaoAtualizada.getAssunto() != null) {
            reuniaoexiste.setAssunto(reuniaoAtualizada.getAssunto());
        }
        if (reuniaoAtualizada.getData() != null) {
            reuniaoexiste.setData(reuniaoAtualizada.getData());
        }
        if (reuniaoAtualizada.getHora() != null) {
            reuniaoexiste.setHora(reuniaoAtualizada.getHora());
        }
        if (reuniaoAtualizada.getFormatoReuniao() != null) {
            reuniaoexiste.setFormatoReuniao(reuniaoAtualizada.getFormatoReuniao());
        }


        reuniaoRepository.save(reuniaoexiste);
        return "Reunião atualizada e reenviada para aprovação";
    }

    public String aceitarReuniao(long id, StatusReuniao novoStatus) {

        Reuniao reuniaoExiste = reuniaoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reunião não encontrada"));

        reuniaoExiste.setStatusReuniao(novoStatus);
        try {
            reuniaoRepository.save(reuniaoExiste);
            return "Status reunião: " + novoStatus.toString().toLowerCase();
        } catch (Exception e) {
            System.err.println("Erro ao salvar a reunião: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar o status da reunião", e);
        }
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