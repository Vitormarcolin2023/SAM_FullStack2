package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
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

    @GetMapping("/findById/{id}")
    public ResponseEntity<Aluno> findById(@PathVariable Long id) {
        try {
            Aluno aluno = alunoService.findById(id);
            return ResponseEntity.ok(aluno);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Status 500
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody Aluno aluno) {
        try {
            Aluno result = alunoService.save(aluno);
            return ResponseEntity.status(HttpStatus.CREATED).body("Aluno cadastrado com sucesso!");
        } catch (Exception ex) {
            // Logar o erro
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Status 500
        }
    }

    //atualizar
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update (@PathVariable Long id, @Valid @RequestBody Aluno alunoUpdate){
        try{
            Aluno alunoAtualizado = alunoService.update(id, alunoUpdate);
            return ResponseEntity.ok(alunoAtualizado);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //deletar
    @DeleteMapping("/delet/{id}")
    public ResponseEntity<?> delete (@PathVariable Long id){
        try{
            alunoService.delete(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Aluno>> saveAll(@RequestBody List<Aluno> alunos) {
        List<Aluno> alunosSalvos = alunoService.saveAll(alunos);
        return ResponseEntity.status(HttpStatus.CREATED).body(alunosSalvos);
    }

    //buscar-por-nome?nome=silva
    @GetMapping("/buscar-por-nome")
    public ResponseEntity<List<Aluno>> getAlunosPorNome(@RequestParam("nome") String nome) {
        List<Aluno> alunosEncontrados = alunoService.buscarPorNome(nome);
        if (alunosEncontrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(alunosEncontrados);
    }

    //alunos/ordenados-por-nome
    @GetMapping("/ordenados-por-nome")
    public ResponseEntity<List<Aluno>> getAlunoOrdenadosPorNome() {
        List<Aluno> alunosOrdenados = alunoService.buscarTodosOrdenadoPorNome();
        return ResponseEntity.ok(alunosOrdenados);
    }

    @GetMapping("/findByEmail")
    public ResponseEntity<Aluno> findByEmail(@RequestParam("email") String email) {
        try {
            Aluno aluno = alunoService.findByEmail(email);
            return ResponseEntity.ok(aluno);
        } catch (RuntimeException e) {
            // Retorna 404 Not Found se o aluno não for encontrado pelo serviço
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            // Retorna 500 para outros erros inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}