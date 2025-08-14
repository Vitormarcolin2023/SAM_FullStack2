package com.br.SAM_FullStack.SAM_FullStack.model;

import ch.qos.logback.core.status.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_mentor")
public class Mentor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;

    @NotBlank(message = "O campo cpf é obrigatório")
    private String cpf;

    @NotBlank(message = "O campo email é obrigatório")
    private String email;

    @NotBlank(message = "O campo senha é obrigatório")
    private String senha;

    /*@NotNull(message = "O tipo de usuário é obrigatório")
    private TipoMentor tipoDeUsuario;
     */
    @NotBlank(message = "O campo telefone é obrigatório")
    private String telefone;

    private String tempoDeExperiencia;

    @NotNull(message = "O status do mentor é obrigatório")
    @Enumerated(EnumType.STRING)
    private StatusMentor tipoDeVinculo;


    @NotNull(message = "A área de atuação é obrigatória")
    private AreaDeAtuacao areaDeAtuacao;


    //private Endereco endereco; criar classe endereço
}

