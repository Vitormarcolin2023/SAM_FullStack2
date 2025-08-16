package com.br.SAM_FullStack.SAM_FullStack.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;
import java.util.List;

@Data
@Entity
@Table(name = "tb_curso")
@Getter
@Setter
@ToString
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @ManyToOne
    @JoinColumn(name = "area_de_atuacao_id", nullable = false)
    @JsonIgnoreProperties("cursos")
    private AreaDeAtuacao areaDeAtuacao;

    @OneToMany(mappedBy = "curso")
    @ToString.Exclude
    @JsonIgnoreProperties("curso")
    private List<Aluno> alunos;

}
