package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.TipoConsulta;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_CONSULTA")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CONSULTA")
    @SequenceGenerator(name = "SEQ_CONSULTA", sequenceName = "SEQ_CONSULTA", allocationSize = 1)
    @Column(name = "ID_CONSULTA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLINICA", nullable = false)
    private Clinica clinica;

    @NotNull
    @FutureOrPresent
    @Column(name = "DATA_CONSULTA", nullable = false)
    private LocalDateTime dataConsulta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 30)
    private TipoConsulta tipo;

    @Size(max = 1000)
    @Column(name = "OBSERVACOES", length = 1000)
    private String observacoes;

    @PositiveOrZero
    @Column(name = "VALOR", precision = 8, scale = 2)
    private BigDecimal valor;
}
