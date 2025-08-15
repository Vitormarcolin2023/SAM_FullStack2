package com.br.SAM_FullStack.SAM_FullStack.model;

import jakarta.persistence.*;
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

    private String nome; // Definido conforme o ID do grupo

    @OneToOne
    @JoinColumn(name = "aluno_admin_id")
    private Aluno alunoAdmin;


    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private List<Aluno> alunos;

}
