package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Aluno>> findAll(){
        try {
            List<Aluno> result = alunoService.findAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Aluno> save(@RequestBody Aluno aluno) {
        try {
            Aluno result = alunoService.save(aluno);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Logar o erro
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Status 500
        }
    }

    //atualizar
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update (@PathVariable Integer id, @Valid @RequestBody Aluno alunoUpdate){
        try{
            Aluno alunoAtualizado = alunoService.update(id, alunoUpdate);
            return ResponseEntity.ok(alunoAtualizado);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //deletar
    @DeleteMapping("/delet/{id}")
    public ResponseEntity<?> delete (@PathVariable Integer id){
        try{
            alunoService.delete(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}