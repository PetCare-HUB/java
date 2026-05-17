package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.TipoConsulta;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CONSULTA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_consulta"
    )
    @SequenceGenerator(
            name = "seq_consulta",
            sequenceName = "SEQ_CONSULTA",
            allocationSize = 1
    )
    @Column(name = "ID_CONSULTA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PET", nullable = false)
    @NotNull
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "ID_CLINICA", nullable = false)
    @NotNull
    private Clinica clinica;

    @Column(name = "DATA_CONSULTA")
    @NotNull
    private LocalDate dataConsulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CONSULTA")
    @NotNull
    private TipoConsulta tipo;

    @Column(name = "DESCRICAO", length = 1000)
    @Size(max = 400)
    private String descricao;

    @Column(name = "DIAGNOSTICO", length = 1000)
    @Size(max = 1000)
    private String diagnostico;

    @Column(name = "VALOR")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal valor;

    @Column(name = "RETORNO_RECOMENDADO")
    private Character retornoRecomendado;

    @Column(name = "DATA_RETORNO")
    private LocalDate dataRetorno;
}
