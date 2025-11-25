package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoDeVinculo {
    CLT,
    PJ,
    TRABALHADOR_AUTÔNOMO,
    TRABALHADOR_TEMPORÁRIO,
    ESTAGIÁRIO,
    SERVIDOR_PÚBLICO_ESTATUÁRIO,
    EMPREGADO_PÚBLICO_REGIME_CLT,
    SERVIDOR_PÚBLCIO_TEMPORÁRIO,
    CARGO_EM_COMISSÃO;


    @JsonCreator
    public static StatusMentor from(String value) {
        return StatusMentor.valueOf(value.toUpperCase());
    }
}
