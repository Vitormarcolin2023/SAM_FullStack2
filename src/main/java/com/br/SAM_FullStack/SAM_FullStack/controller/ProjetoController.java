package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.ProjetoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    @RestController
    @RequestMapping("/projetos")
    @RequiredArgsConstructor
    public class ProjetoController {

    private ProjetoService projetoService;


    @GetMapping("/findAll")
    public ResponseEntity<List<Projeto>> listAll() {
        try {
            var result = projetoService.listAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        try {
            Projeto projeto = projetoService.findById(id);
            return ResponseEntity.ok(projeto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Projeto com ID " + id + " não encontrado.");
        }
    }
    @GetMapping("/buscar-por-nome")
    public List<Projeto> buscarPorNome(@RequestParam String nome){
        return projetoService.buscarPorNome(nome);
    }
    @GetMapping("/buscar-por-atuacao")
    public List<Projeto> buscarPorAtuacao (@RequestParam AreaDeAtuacao areaDeAtuacao){
        return projetoService.buscarPorAreaAtuacao(areaDeAtuacao);
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(@RequestBody Projeto projeto) {
        try {
            Projeto savedProjeto = projetoService.save(projeto);
            return ResponseEntity.ok(savedProjeto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao salvar projeto: " + e.getMessage());
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Object> update (@PathVariable Long id, @RequestBody Projeto projeto) {
        try {
            Projeto projetoAtualizado = projetoService.update(id, projeto);
            return ResponseEntity.ok(projetoAtualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não foi possível atualizar o projeto. Verifique o ID ou os dados fornecidos.");
        }
    }

        @DeleteMapping("/delete/{id}")
        public ResponseEntity<String> delete (@PathVariable Long id){
            try {
                projetoService.delete(id);
                return ResponseEntity.ok("Projeto excluído com sucesso");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Não foi possível excluir o projeto. O ID fornecido não existe.");
            }
        }
    }


