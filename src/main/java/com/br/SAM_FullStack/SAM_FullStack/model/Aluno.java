package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "tb_aluno")
@Getter
@Setter
@ToString
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private Integer ra;
    private String senha;
    private String email;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

}