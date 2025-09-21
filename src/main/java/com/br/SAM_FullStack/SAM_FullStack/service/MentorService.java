package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;


    @Autowired
    private EmailService emailService;

    public MentorService(MentorRepository mentorRepository){
        this.mentorRepository = mentorRepository;
    }

    //listar
    public List<Mentor> listAll(){
        return mentorRepository.findAll();
    }

    //buscar por id
    public Mentor findById(long id){
        return mentorRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Mentor não encontrado"));
    }

    // Salvar
    public Mentor save(Mentor mentor) {
        // Status inicial até a coordenação aprovar
        mentor.setStatusMentor(StatusMentor.PENDENTE);

        //Envio de email
        String destinatario = mentor.getEmail();
        String assunto = "Bem-vindo(a) ao SAM - Cadastro em Análise";
        Map<String, Object> variaveis = Map.of("nomeMentor", mentor.getNome());
        String template = "emails/boasVindasMentor";
        emailService.enviarEmailComTemplate(destinatario, assunto, template, variaveis);

        return mentorRepository.save(mentor);
    }

    public Mentor update(Long id, Mentor mentorUpdate) {
        Mentor mentorExistente = findById(id);

        // Atualiza campos apenas se vierem preenchidos
        if (mentorUpdate.getNome() != null) mentorExistente.setNome(mentorUpdate.getNome());
        if (mentorUpdate.getCpf() != null) mentorExistente.setCpf(mentorUpdate.getCpf());
        if (mentorUpdate.getEmail() != null) mentorExistente.setEmail(mentorUpdate.getEmail());
        if (mentorUpdate.getSenha() != null) mentorExistente.setSenha(mentorUpdate.getSenha());
        if (mentorUpdate.getTipoDeVinculo() != null) mentorExistente.setTipoDeVinculo(mentorUpdate.getTipoDeVinculo());
        if (mentorUpdate.getTempoDeExperiencia() != null) mentorExistente.setTempoDeExperiencia(mentorUpdate.getTempoDeExperiencia());
        if (mentorUpdate.getAreaDeAtuacao() != null) mentorExistente.setAreaDeAtuacao(mentorUpdate.getAreaDeAtuacao());
        if (mentorUpdate.getEndereco() != null) mentorExistente.setEndereco(mentorUpdate.getEndereco());
        if (mentorUpdate.getResumo() != null) mentorExistente.setResumo(mentorUpdate.getResumo());

        return mentorRepository.save(mentorExistente);
    }


    //deletar
    public void delete(Long id){
        Mentor mentor = findById(id);
        mentorRepository.delete(mentor);
    }

    public String updateStatus(long id, String statusString){
        Mentor mentor = mentorRepository.findById(id).orElseThrow(() -> new RuntimeException("Mentor não encontrado."));

        StatusMentor novoStatus = StatusMentor.valueOf(statusString.toUpperCase());

        mentor.setStatusMentor(novoStatus);
        mentorRepository.save(mentor);

        return "Status do mentor atualizado com sucesso!";
    }

    public Mentor findByEmail(String email) {
        // Usa o repositório para buscar o mentor por e-mail e retorna o objeto ou null
        Optional<Mentor> mentor = mentorRepository.findByEmail(email);
        return mentor.orElse(null);
    }
}
