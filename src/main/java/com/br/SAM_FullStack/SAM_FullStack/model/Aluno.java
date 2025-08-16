package com.br.SAM_FullStack.SAM_FullStack.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    private String nome;
    private Integer ra;
    private String senha;
    private String email;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnoreProperties("alunos")
    private Curso curso;
}