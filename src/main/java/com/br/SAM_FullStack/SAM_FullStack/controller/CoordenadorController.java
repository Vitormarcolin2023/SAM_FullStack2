package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.CoordenadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordenador")
public class CoordenadorController {
    @Autowired
    private CoordenadorService coordenadorService;

    @PostMapping("/save")
    public ResponseEntity<Coordenador> save(@Valid @RequestBody CoordenadorDTO coordenadorDTO){
        try {
            Coordenador novoCoordenador = this.coordenadorService.save(coordenadorDTO);
            return new ResponseEntity<>(novoCoordenador, HttpStatus.CREATED);
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

    @PutMapping("/inativarMentor/{id}")
    public ResponseEntity<String> inativarMentor(@PathVariable long id){
        try {
            String mensagem = this.coordenadorService.inativarMentor(id);
            return new ResponseEntity<>(mensagem, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/mentores")
    public ResponseEntity<List<Mentor>> findAllMentores(){
        List<Mentor> mentores = this.coordenadorService.findAllMentores();
        return new ResponseEntity<>(mentores, HttpStatus.OK);
    }

    @GetMapping("/projetos")
    public ResponseEntity<List<Projeto>> findAllProjetos(){
        List<Projeto> projetos = this.coordenadorService.findAllProjetos();
        return new ResponseEntity<>(projetos, HttpStatus.OK);
    }
}