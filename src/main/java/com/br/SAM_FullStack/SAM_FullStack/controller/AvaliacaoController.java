package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Avaliacao;
import com.br.SAM_FullStack.SAM_FullStack.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    /**
     * Endpoint para um aluno submeter uma nova avaliação para um projeto.
     * O ID do projeto é passado na URL.
     * O corpo da avaliação (respostas, comentario) é passado no Body.
     */
    @PostMapping("/projeto/{projetoId}")
    public ResponseEntity<Avaliacao> criarAvaliacao(
            @PathVariable Long projetoId,
            @Valid @RequestBody Avaliacao avaliacao) {

        avaliacao.setId(null);
        avaliacao.setMedia(null); // Será calculado no service

        Avaliacao avaliacaoSalva = avaliacaoService.salvarAvaliacao(avaliacao, projetoId);

        // Retorna 201 Created com a avaliação salva (agora com a média calculada)
        return ResponseEntity.status(201).body(avaliacaoSalva);
    }
}