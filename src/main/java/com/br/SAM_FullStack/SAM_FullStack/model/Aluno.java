package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_aluno")
@Getter
@Setter
@ToString
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;

    @NotNull(message = "O campo RA é obrigatório")
    private Integer ra;

    @NotBlank(message = "O campo senha é obrigatório")
    private String senha;

    @NotBlank(message = "O campo e-mail é obrigatório")
    @Email(message = "O e-mail informado não é válido") // Validação extra para formato de e-mail
    private String email;

    @NotNull(message = "O curso do aluno é obrigatório")
    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnoreProperties("alunos")
    private Curso curso;

    @Enumerated(EnumType.STRING)
    private StatusAlunoGrupo statusAlunoGrupo;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "grupo_id")
    @JsonIgnore
    private Grupo grupo;

}