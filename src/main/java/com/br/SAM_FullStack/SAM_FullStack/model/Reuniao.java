package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_reuniao")
public class Reuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O campo 'assunto' não pode ser nulo")
    private String assunto;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "É necessário informar a data da reunião")
    private Date data;

    @JsonFormat(pattern = "HH:mm:ss")
    @NotNull(message = "É necessário informar a hora da reunião")
    private Time hora;


    @Enumerated(EnumType.STRING)
    private FormatoReuniao formatoReuniao;
    @Enumerated(EnumType.STRING)
    private StatusReuniao statusReuniao;

    @ManyToOne
    @JoinColumn(name = "mentor_admin_id")
    @JsonIgnore
    private Mentor mentor;


    @ManyToOne
    @JoinColumn(name = "grupo_id")
    @JsonIgnore
    private Grupo grupo;

}
