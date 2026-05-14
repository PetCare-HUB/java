package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.TipoConsulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "CONSULTA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONSULTA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "ID_CLINICA", nullable = false)
    private Clinica clinica;

    @Column(name = "DATA_CONSULTA")
    private LocalDate dataConsulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO")
    private TipoConsulta tipo;

    @Column(name = "DESCRICAO", length = 1000)
    private String descricao;

    @Column(name = "DIAGNOSTICO", length = 1000)
    private String diagnostico;

    @Column(name = "VALOR")
    private Double valor;

    @Column(name = "RETORNO_RECOMENDADO")
    private Boolean retornoRecomendado;

    @Column(name = "DATA_RETORNO")
    private LocalDate dataRetorno;
}
