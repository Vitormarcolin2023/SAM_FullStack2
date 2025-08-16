package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.service.CoordenadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coordenador")
public class CoordenadorController {
    @Autowired
    private CoordenadorService coordenadorService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Coordenador coordenador){
        try {
            String mensagem = this.coordenadorService.save(coordenador);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Coordenador coordenador, @PathVariable long id){
        try {
            String mensagem = this.coordenadorService.update(coordenador, id);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/ativarMentor/{id}")
    public ResponseEntity<String> ativarMentor(@PathVariable long id){
        try {
            String mensagem = this.coordenadorService.ativarMentor(id);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}