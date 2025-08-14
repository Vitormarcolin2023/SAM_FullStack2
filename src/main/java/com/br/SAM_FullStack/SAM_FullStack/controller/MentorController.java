package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.service.MentorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/mentores")
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

    @GetMapping("/{id}")
    public ResponseEntity<Mentor> findById(@PathVariable Long id){
        return ResponseEntity.ok(mentorService.findById(id));
    }

    /*
    @PostMapping
    public ResponseEntity<Mentor> save(@RequestBody Mentor mentor){
        return ResponseEntity.ok(mentorService.save(mentor));
    }
     */

    @PutMapping("/{id}")
    public ResponseEntity<Mentor> update(@PathVariable Long id, @RequestBody Mentor mentor){
        return ResponseEntity.ok(mentorService.update(id, mentor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        mentorService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
