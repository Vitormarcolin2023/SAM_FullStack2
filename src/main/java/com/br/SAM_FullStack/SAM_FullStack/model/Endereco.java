package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo rua é obrigatório")
    @Column(length = 45, nullable = false)
    private String rua;

    @NotBlank(message = "O campo número é obrigatório")
    @Column(nullable = false)
    private String numero; // alterei para String para aceitar "123A" "S/N" etc.

    @NotBlank(message = "O campo bairro é obrigatório")
    @Column(length = 45, nullable = false)
    private String bairro;

    @NotBlank(message = "O campo cidade é obrigatório")
    @Column(length = 45, nullable = false)
    private String cidade;

    @NotBlank(message = "O campo estado é obrigatório")
    @Column(length = 45, nullable = false)
    private String estado;

    @NotBlank(message = "O campo CEP é obrigatório")
    @Column(length = 45, nullable = false)
    private String cep;

    // Relacionamento com Mentor
    @OneToOne(mappedBy = "endereco")
    @JsonIgnoreProperties("endereco")
    private Mentor mentor;

    @Override
    public String toString() {
        return rua + ", " + numero + " - " + bairro + ", " + cidade + " - " + estado + ", CEP: " + cep;
    }
}
