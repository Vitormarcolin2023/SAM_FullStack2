package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Reuniao;
import com.br.SAM_FullStack.SAM_FullStack.service.ReuniaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reunioes")
public class ReuniaoController1 {

    private final ReuniaoService reuniaoService;

    // Listar todas as reuniões
    @GetMapping("/findAll")
    public ResponseEntity<List<Reuniao>> findAll() {
        try {
            List<Reuniao> result = reuniaoService.findAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar reunião por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<Reuniao> findById(@PathVariable long id) {
        try {
            Reuniao result = reuniaoService.findById(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Listar reuniões de um grupo
    @GetMapping("/findByGrupo/{id}")
    public ResponseEntity<List<Reuniao>> findAllByGrupo(@PathVariable long id) {
        try {
            List<Reuniao> result = reuniaoService.findAllByGrupo(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Listar reuniões de um mentor
    @GetMapping("/findByMentor/{idMentor}")
    public ResponseEntity<List<Reuniao>> findAllByMentor(@PathVariable long idMentor) {
        try {
            List<Reuniao> result = reuniaoService.findAllByMentor(idMentor);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // Criar uma reunião
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Reuniao reuniao) {
        try {
            String result = reuniaoService.save(reuniao);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Atualizar reunião existente
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Reuniao reuniao) {
        try {
            String result = reuniaoService.update(id, reuniao);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Deletar reunião
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            String result = reuniaoService.delete(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
