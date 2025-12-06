package com.br.SAM_FullStack.SAM_FullStack.dto;

import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjetoPendenteDTO {
    private Long id;
    private String tituloProjeto;
    private String nomeCurso; // Vamos preencher com a Área de Atuação

    // Construtor que converte a Entidade em DTO
    public ProjetoPendenteDTO(Projeto projeto) {
        this.id = projeto.getId();
        this.tituloProjeto = projeto.getNomeDoProjeto();

        // Verificação de segurança para não quebrar se a área for nula
        if (projeto.getAreaDeAtuacao() != null) {
            this.nomeCurso = projeto.getAreaDeAtuacao().getNome();
        } else {
            this.nomeCurso = "Área não definida";
        }
    }
}