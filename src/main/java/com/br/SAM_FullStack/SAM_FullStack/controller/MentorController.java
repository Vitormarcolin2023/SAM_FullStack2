package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.service.MentorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentores")
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService){
        this.mentorService=mentorService;
    }

    //listar
    @GetMapping("/findAll")
    public ResponseEntity<List<Mentor>> listAll() {
        try {
            var result = mentorService.listAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    //buscar pelo Id
    @GetMapping("/findById/{id}")
    public ResponseEntity<Mentor> findById(@PathVariable Long id){
        return ResponseEntity.ok(mentorService.findById(id));
    }

    //salvar
    @PostMapping("/save")
    public ResponseEntity<Mentor> save(@RequestBody Mentor mentor){
        return ResponseEntity.ok(mentorService.save(mentor));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Mentor mentor) {
        try {
            // Tenta realizar a atualização do mentor
            Mentor mentorAtualizado = mentorService.update(id, mentor);
            // Se a atualização for bem-sucedida, retorna o mentor com status 200 OK
            return ResponseEntity.ok(mentorAtualizado);
        } catch (Exception e) {
            // Se houver um erro, retorna uma mensagem com status 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível atualizar o mentor. Verifique o ID ou os dados fornecidos.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            // Tenta excluir o mentor
            mentorService.delete(id);

            // Se a exclusão for bem-sucedida, retorna a mensagem de sucesso
            return ResponseEntity.ok("Mentor excluído com sucesso");
        } catch (Exception e) {
            // Se ocorrer um erro (por exemplo, mentor não encontrado), retorna a mensagem de erro com status 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível excluir o mentor. O ID fornecido não existe.");
        }
    }

}
