package com.br.SAM_FullStack.SAM_FullStack.model;

import jakarta.persistence.*;
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
    private String assunto;
    private Date data;
    private Time hora;

    @Enumerated(EnumType.STRING)
    private FormatoReuniao formatoReuniao;
    @Enumerated(EnumType.STRING)
    private StatusReuniao statusReuniao;

    @OneToOne
    @JoinColumn(name = "mentor_admin_id")
    private Mentor mentor;

    @OneToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

}
