package com.br.SAM_FullStack.SAM_FullStack.model;

import jakarta.persistence.*; // Importa todas as anotações do JPA, incluindo @Id
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_mentor")
public class Mentor {

    @Id // Anotação correta do JPA
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

    @NotBlank(message = "O campo telefone é obrigatório")
    private String telefone;

    private String tempoDeExperiencia;

    @Enumerated(EnumType.STRING)
    private StatusMentor statusMentor;

    @NotNull(message = "O tipo de vínculo do mentor é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoDeVinculo tipoDeVinculo;

    @NotNull(message = "A área de atuação é obrigatória")
    @Enumerated(EnumType.STRING) // Adicione esta anotação se AreaDeAtuacao for um enum
    private AreaDeAtuacao areaDeAtuacao;

    // Relacionamento com Endereco
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;


}