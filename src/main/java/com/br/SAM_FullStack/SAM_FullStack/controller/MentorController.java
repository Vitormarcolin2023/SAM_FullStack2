package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.service.MentorService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentores")
@CrossOrigin("*")
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
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        try {
            Mentor mentor = mentorService.findById(id);
            return ResponseEntity.ok(mentor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mentor com ID " + id + " não encontrado.");
        }
    }

    //salvar
    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody Mentor mentor) {
        try {
            Mentor savedMentor = mentorService.save(mentor);
            // Retorna o mentor salvo se a operação for bem-sucedida.
            return ResponseEntity.status(HttpStatus.CREATED).body("Mentor cadastrado com sucesso!");

        } catch (Exception e) {
            // Retorna uma mensagem de erro e o status BAD_REQUEST em caso de falha.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao salvar mentor: " + e.getMessage());
        }
    }


    //update
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

    //delete
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

    @GetMapping("/me") //mentor email
    public ResponseEntity<Mentor> getMentorProfile(Authentication authentication) {
        // Obtém o e-mail do usuário autenticado a partir do token
        String email = authentication.name();

        // Busca o mentor pelo e-mail usando o seu MentorService
        Mentor mentor = mentorService.findByEmail(email);

        if (mentor != null) {
            return ResponseEntity.ok(mentor);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
