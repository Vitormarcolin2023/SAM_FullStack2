package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private MentorService mentorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProjetoService projetoService;

    public String save(Professor professor){

        String senhaEncrip = passwordEncoder.encode(professor.getSenha());
        professor.setSenha(senhaEncrip);

        this.professorRepository.save(professor);
        return "Professor salvo com sucesso!";
    }

    public String update(Professor professor, long id){
        professor.setId(id);

        String senhaEncript = passwordEncoder.encode(professor.getSenha());
        professor.setSenha(senhaEncript);

        this.professorRepository.save(professor);
        return "Professor atualizado com sucesso!";
    }

    public String delete(long id){
        this.professorRepository.deleteById(id);
        return "Professor deletado com sucesso!";
    }

    public List<Professor> findAll(){
        List<Professor> lista = this.professorRepository.findAll();
        return lista;
    }

    public List<Mentor> findAllMentores(){
        return this.mentorService.listAll();
    }

    public List<Projeto> findAllProjetos(){
        return this.projetoService.listAll();
    }



}