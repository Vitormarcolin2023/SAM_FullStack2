package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O campo 'resposta1' não pode ser nulo")
    private Integer resposta1;

    @NotNull(message = "O campo 'resposta2' não pode ser nulo")
    private Integer resposta2;

    @NotNull(message = "O campo 'resposta3' não pode ser nulo")
    private Integer resposta3;

    @NotNull(message = "O campo 'resposta4' não pode ser nulo")
    private Integer resposta4;

    @NotNull(message = "O campo 'resposta5' não pode ser nulo")
    private Integer resposta5;

    @NotNull(message = "O campo 'media' não pode ser nulo")
    private Double media;

    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id")
    @NotNull(message = "A avaliação deve estar associada a um projeto")
    @JsonIgnore
    private Projeto projeto;

}