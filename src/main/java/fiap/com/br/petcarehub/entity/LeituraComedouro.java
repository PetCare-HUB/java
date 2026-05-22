package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_LEITURA_COMEDOURO")
public class LeituraComedouro {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LEITURA_COMEDOURO")
    @SequenceGenerator(name = "SEQ_LEITURA_COMEDOURO", sequenceName = "SEQ_LEITURA_COMEDOURO", allocationSize = 1)
    @Column(name = "ID_LEITURA_COMEDOURO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "NIVEL_RACAO_PCT", nullable = false)
    private Integer nivelRacaoPct;

    @NotNull
    @PositiveOrZero
    @Column(name = "PESO_CONSUMIDO_G", nullable = false, precision = 8, scale = 2)
    private BigDecimal pesoConsumidoG;

    @NotNull
    @Column(name = "TIMESTAMP_LEITURA", nullable = false)
    private LocalDateTime timestampLeitura;
}
