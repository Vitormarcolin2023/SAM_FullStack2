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
@RequestMapping("/api/reunioes")
public class ReuniaoController {

    private final ReuniaoService reuniaoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Reuniao>> findAll(){
        try {
            var result = reuniaoService.findAll();
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Reuniao reuniao){
        try {
            String result = reuniaoService.save(reuniao);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
