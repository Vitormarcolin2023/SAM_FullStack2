package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;

    public String save(Professor professor){
        this.professorRepository.save(professor);
        return "Professor salvo com sucesso!";
    }

    public String update(Professor professor, long id){
        professor.setId(id);
        this.professorRepository.save(professor);
        return "Professor atualizado com sucesso!";
    }

}