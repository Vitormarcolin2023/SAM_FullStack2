package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.server.ExportException;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {
    @Autowired
    private ProfessorService professorService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Professor professor){
        try {
            String mensagem = this.professorService.save(professor);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Professor professor, @PathVariable long id){
        try{
            String mensagem = this.professorService.update(professor, id);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

    }

}