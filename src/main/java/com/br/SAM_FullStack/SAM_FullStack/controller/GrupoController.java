package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.service.GrupoService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/grupos")
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
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody GrupoDTO grupo) {
        try {
            String result = grupoService.save(grupo);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // Status 500
        }
    }

    @DeleteMapping("/deleteAlunoById/admin/{}/aluno/{}")
    public ResponseEntity<String> deleteAlunoById(@PathVariable Integer admin, @PathVariable Integer aluno){
        try {
            String result = grupoService.excluirAlunoGrupo(admin, aluno);
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /*
    @DeleteMapping("/delet/{id}")
    public ResponseEntity<?> delete (@PathVariable Integer id){
        try{
            alunoService.delete(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
}
