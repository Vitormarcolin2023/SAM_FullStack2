package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespostaLoginDTO {

    private String token;

    public RespostaLoginDTO(String token){
        this.token = token;
    }
}
