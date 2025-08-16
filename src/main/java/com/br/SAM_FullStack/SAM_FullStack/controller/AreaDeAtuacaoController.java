package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.service.AreaDeAtuacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
public class AreaDeAtuacaoController {

    private final AreaDeAtuacaoService areaDeAtuacaoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<AreaDeAtuacao>> findAll(){
        try {
            List<AreaDeAtuacao> result = areaDeAtuacaoService.findAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<AreaDeAtuacao> save(@RequestBody AreaDeAtuacao areaDeAtuacao) {
        try {
            AreaDeAtuacao result = areaDeAtuacaoService.save(areaDeAtuacao);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Logar o erro
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Status 500
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update (@PathVariable Long id, @Valid @RequestBody AreaDeAtuacao areaDeAtuacaoUpdate){
        try{
            AreaDeAtuacao areaDeAtuacaoAtualizado = areaDeAtuacaoService.update(id, areaDeAtuacaoUpdate);
            return ResponseEntity.ok(areaDeAtuacaoAtualizado);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delet/{id}")
    public ResponseEntity<?> delete (@PathVariable Long id){
        try{
            areaDeAtuacaoService.delete(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

}
