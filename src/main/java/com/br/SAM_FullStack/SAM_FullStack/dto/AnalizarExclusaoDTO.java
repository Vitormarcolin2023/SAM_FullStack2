package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnalizarExclusaoDTO {
    private long idProf;
    private long idGrupo;
    private Long idAluno;
    private boolean resposta;
}
