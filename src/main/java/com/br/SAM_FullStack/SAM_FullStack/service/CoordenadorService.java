package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordenadorService {

    private final CoordenadorRepository coordenadorRepository;
    private final CursoRepository cursoRepository; // Adicionado
    private final MentorService mentorService;
    private final ProjetoService projetoService;

    @Transactional
    public Coordenador save(CoordenadorDTO coordenadorDTO) {
        Coordenador coordenador = new Coordenador();
        coordenador.setNome(coordenadorDTO.getNome());
        coordenador.setEmail(coordenadorDTO.getEmail());
        coordenador.setSenha(coordenadorDTO.getSenha());

        List<Curso> cursos = cursoRepository.findAllById(coordenadorDTO.getCursosIds());

        for (Curso curso : cursos) {
            curso.setCoordenador(coordenador);
        }

        coordenador.setCursos(cursos);

        return coordenadorRepository.save(coordenador);
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

    public String inativarMentor(long mentorId){
        try {
            String mensagem = this.mentorService.updateStatus(mentorId, "PENDENTE");
            return "Mentor inativado com sucesso.";
        } catch (Exception e) {
            return "Erro ao tentar inativar o mentor.";
        }
    }

    public List<Mentor> findAllMentores(){
        return this.mentorService.listAll();
    }

    public List<Projeto> findAllProjetos(){
        return this.projetoService.listAll();
    }
}