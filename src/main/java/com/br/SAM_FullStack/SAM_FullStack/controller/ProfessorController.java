package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Professor professor) {
        String mensagem = this.professorService.save(professor);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Professor professor, @PathVariable long id) {
        String mensagem = this.professorService.update(professor, id);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        String mensagem = this.professorService.delete(id);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Professor>> findAll() {
        List<Professor> lista = this.professorService.findAll();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/mentores")
    public ResponseEntity<List<Mentor>> findAllMentores() {
        List<Mentor> mentores = this.professorService.findAllMentores();
        return new ResponseEntity<>(mentores, HttpStatus.OK);
    }

    @GetMapping("/projetos")
    public ResponseEntity<List<Projeto>> findAllProjetos() {
        List<Projeto> projetos = this.professorService.findAllProjetos();
        return new ResponseEntity<>(projetos, HttpStatus.OK);
    }

    @GetMapping("/buscar-por-email")
    public ResponseEntity<Professor> getProfessorPorEmail(@RequestParam String email) {
        Professor professor = this.professorService.findByEmail(email);
        return new ResponseEntity<>(professor, HttpStatus.OK);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Professor> getProfessorPorId(@PathVariable Long id) {
        try {
            Professor professor = this.professorService.findById(id);
            return new ResponseEntity<>(professor, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}