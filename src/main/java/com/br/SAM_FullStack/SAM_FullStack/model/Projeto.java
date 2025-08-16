package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_projeto")

public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "O campo nome do projeto é obrigatório")
    @Column(name = "nome_do_projeto", nullable = false, length = 45)
    private String nomeDoProjeto;

    @NotBlank(message = "Favor adicionar uma descrição do projeto")
    @Column(name = "descricao", nullable = false, length = 100)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "area_de_atuacao")
    private AreaDeAtuacao areaDeAtuacao;

    @NotNull(message = "A data de inicio é obrigatória")
    @Column(name = "data_inicio_projeto", nullable = false)
    private LocalDate dataInicioProjeto;

    @NotNull(message = "A data para conclusão do projeto é obrigatório")
    @Column(name = "data_final_projeto", nullable = false)
    private LocalDate dataFinalProjeto;

    @Min(value = 1, message = "É obrigatório adicionar a quantidade de alunos no projeto")
    @Column(name = "tamanho_do_grupo")
    private int tamanhoDoGrupo;

    @NotBlank(message = "É obrigatório adicionar o periodo da faculdade")
    @Column(name = "periodo", nullable = false, length = 45)
    private String periodo;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    @JsonIgnoreProperties("projetos")
    private Mentor mentor;

    private String statusProjeto;

   /* @ManyToMany
    @JoinTable(
            name = "projeto_aluno",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )

    @JsonIgnoreProperties("projetos")
    private List<Aluno> alunos;
}
*/

}
