package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Avaliacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.AvaliacaoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ProjetoRepository projetoRepository;

    @Transactional
    public Avaliacao salvarAvaliacao(Avaliacao avaliacao, Long projetoId) {

        // 1. Associa o Projeto à Avaliação
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + projetoId));

        avaliacao.setProjeto(projeto);

        // 2. Calcula a média *desta* avaliação (r1 a r6)
        double mediaCalculada = (
                avaliacao.getResposta1() +
                        avaliacao.getResposta2() +
                        avaliacao.getResposta3() +
                        avaliacao.getResposta4() +
                        avaliacao.getResposta5() +
                        avaliacao.getResposta6()
        ) / 6.0;

        // 3. Define a média no objeto Avaliacao
        avaliacao.setMedia(arredondar(mediaCalculada));

        // 4. Salva a avaliação (com sua média) no banco
        return avaliacaoRepository.save(avaliacao);

    }

    // Helper para arredondar as médias para 2 casas decimais
    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}