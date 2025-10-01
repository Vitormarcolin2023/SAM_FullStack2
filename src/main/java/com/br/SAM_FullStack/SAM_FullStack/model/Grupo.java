package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O campo 'nome' não pode ser nulo")
    private String nome;

    @Enumerated(EnumType.STRING)
    private StatusGrupo statusGrupo;

    @OneToOne
    @JoinColumn(name = "aluno_admin_id")
    private Aluno alunoAdmin;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private List<Aluno> alunos;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reuniao> reunioes = new ArrayList<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("grupo")
    private List<Projeto> projetos;


}
