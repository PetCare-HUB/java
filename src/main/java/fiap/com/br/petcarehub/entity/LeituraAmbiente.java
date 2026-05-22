package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_LEITURA_AMBIENTE")
public class LeituraAmbiente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LEITURA_AMBIENTE")
    @SequenceGenerator(name = "SEQ_LEITURA_AMBIENTE", sequenceName = "SEQ_LEITURA_AMBIENTE", allocationSize = 1)
    @Column(name = "ID_LEITURA_AMBIENTE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @NotNull
    @Column(name = "TEMPERATURA_AMBIENTE", nullable = false, precision = 5, scale = 2)
    private BigDecimal temperaturaAmbiente;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "UMIDADE_PCT", nullable = false)
    private Integer umidadePct;

    @NotNull
    @Min(0)
    @Column(name = "QUALIDADE_AR_PPM", nullable = false)
    private Integer qualidadeArPpm;

    @NotNull
    @Column(name = "PET_PRESENTE", nullable = false)
    private Boolean petPresente;

    @NotNull
    @Column(name = "TIMESTAMP_LEITURA", nullable = false)
    private LocalDateTime timestampLeitura;
}
