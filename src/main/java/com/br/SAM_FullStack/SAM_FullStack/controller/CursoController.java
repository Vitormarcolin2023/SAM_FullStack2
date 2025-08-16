package com.br.SAM_FullStack.SAM_FullStack.controller;


import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Curso>> findAll(){
        try {
            List<Curso> result = cursoService.findAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Curso> save(@RequestBody Curso curso) {
        try {
            Curso result = cursoService.save(curso);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Logar o erro
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Status 500
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update (@PathVariable Long id, @Valid @RequestBody Curso cursoUpdate){
        try{
            Curso cursoAtualizado = cursoService.update(id, cursoUpdate);
            return ResponseEntity.ok(cursoAtualizado);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delet/{id}")
    public ResponseEntity<?> delete (@PathVariable Long id){
        try{
            cursoService.delete(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Curso>> saveAll(@RequestBody List<Curso> cursos) {
        List<Curso> cursosSalvos = cursoService.saveAll(cursos);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursosSalvos);
    }

    //buscar-por-curso?curso=sistemas
    @GetMapping("/buscar-por-curso")
    public ResponseEntity<List<Curso>> getCyrsosPorNome(@RequestParam("curso") String curso) {
        List<Curso> cursosEncontrados = cursoService.buscarPorCurso(curso);
        if (cursosEncontrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cursosEncontrados);
    }
}
