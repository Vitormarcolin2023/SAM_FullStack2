package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.repository.AreaDeAtuacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.geom.Area;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AreaDeAtuacaoService {

    private final AreaDeAtuacaoRepository areaDeAtuacaoRepository;


    public AreaDeAtuacao findById(Long id) {
        return areaDeAtuacaoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Area de Atuação não encontrada com ID: " + id));
    }

    public List<AreaDeAtuacao> findAll() {
        return areaDeAtuacaoRepository.findAll();
    }

    public AreaDeAtuacao save(AreaDeAtuacao areaDeAtuacao) {
        return areaDeAtuacaoRepository.save(areaDeAtuacao);
    }


}
