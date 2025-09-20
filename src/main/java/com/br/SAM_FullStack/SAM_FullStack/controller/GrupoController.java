package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.AdicionarAlunoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.AnalizarExclusaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
import com.br.SAM_FullStack.SAM_FullStack.service.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grupos")
public class GrupoController {

    private final GrupoService grupoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Grupo>> findAll(){
        try {
            List<Grupo> result = grupoService.findAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Grupo> findById(@PathVariable long id){
        try {
            Grupo result = grupoService.findById(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<GrupoDTO> save(@RequestBody GrupoDTO grupo) {
        GrupoDTO result = grupoService.save(grupo);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /*
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody GrupoDTO grupo) {
        try {
            String result = grupoService.save(grupo);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST); // Status 500
        }
    }
     */



    @PostMapping("/adicionar-aluno")
    public ResponseEntity<String> adicionarAluno(@RequestBody AdicionarAlunoDTO dto) {
        try {
            String result = grupoService.adicionarAlunoAoGrupo(dto.getIdAdmin(), dto.getIdGrupo(), dto.getIdAluno());
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteAlunoById/admin/{admin}/aluno/{aluno}")
    public ResponseEntity<String> deleteAlunoById(@PathVariable Long admin, @PathVariable Long aluno){
        try {
            String result = grupoService.excluirAlunoGrupo(admin, aluno);
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/findSolicitacoes")
    public ResponseEntity<List<Grupo>> findByAlunosStatusAlunoGrupo(){
        try {
            List<Grupo> result = grupoService.findByAlunosStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // para professor aceitar ou recusar a exclusao do aluno do grupo
    @PutMapping("analizarSolicitacao")
    public ResponseEntity<String> analizarExclusaoAluno(@RequestBody AnalizarExclusaoDTO pedido) {
        try {
            String result = grupoService.analizarExclusaoAluno(
                    pedido.getSenhaProf(),
                    pedido.getIdGrupo(),
                    pedido.getIdAluno(),
                    pedido.isResposta()
            );
            return ResponseEntity.ok(result);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }


    // deletar grupo -> PROFESSOR
    @DeleteMapping("delete/{idGrupo}/professor/{idProfessor}")
    public ResponseEntity<String> deletarGrupo(@PathVariable Long idGrupo, @PathVariable Long idProfessor){
        try{
            String result = grupoService.deletarGrupo(idGrupo, idProfessor);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
