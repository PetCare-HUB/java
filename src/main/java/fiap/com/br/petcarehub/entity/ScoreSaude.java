package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.CategoriaScore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_SCORE_SAUDE")
public class ScoreSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SCORE_SAUDE")
    @SequenceGenerator(name = "SEQ_SCORE_SAUDE", sequenceName = "SEQ_SCORE_SAUDE", allocationSize = 1)
    @Column(name = "ID_SCORE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "SCORE", nullable = false)
    private Integer score;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORIA", nullable = false, length = 20)
    private CategoriaScore categoria;

    @Size(max = 1000)
    @Column(name = "OBSERVACAO", length = 1000)
    private String observacao;

    @CreationTimestamp
    @Column(name = "DATA_CALCULO", nullable = false)
    private LocalDateTime dataCalculo;
}
