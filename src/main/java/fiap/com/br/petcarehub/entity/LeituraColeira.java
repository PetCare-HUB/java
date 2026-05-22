package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.StatusAtividade;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_LEITURA_COLEIRA")
public class LeituraColeira {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LEITURA_COLEIRA")
    @SequenceGenerator(name = "SEQ_LEITURA_COLEIRA", sequenceName = "SEQ_LEITURA_COLEIRA", allocationSize = 1)
    @Column(name = "ID_LEITURA_COLEIRA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_ATIVIDADE", nullable = false, length = 30)
    private StatusAtividade statusAtividade;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "NIVEL_BATERIA", nullable = false)
    private Integer nivelBateria;

    @NotNull
    @Column(name = "TIMESTAMP_LEITURA", nullable = false)
    private LocalDateTime timestampLeitura;
}
