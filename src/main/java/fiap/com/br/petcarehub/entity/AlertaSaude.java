package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.config.BooleanToCharacterConverter;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ALERTA_SAUDE")
public class AlertaSaude {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ALERTA_SAUDE")
    @SequenceGenerator(name = "SEQ_ALERTA_SAUDE", sequenceName = "SEQ_ALERTA_SAUDE", allocationSize = 1)
    @Column(name = "ID_ALERTA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ALERTA", nullable = false, length = 40)
    private TipoAlerta tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_ALERTA", nullable = false, length = 20)
    private NivelAlerta nivel;

    @NotBlank
    @Size(max = 500)
    @Column(name = "MENSAGEM", nullable = false, length = 500)
    private String mensagem;

    @NotNull
    @Column(name = "VALOR_DETECTADO", precision = 10, scale = 2)
    private BigDecimal valorDetectado;

    @NotNull
    @Column(name = "LIMITE_REFERENCIA", precision = 10, scale = 2)
    private BigDecimal limiteReferencia;

    @Builder.Default
    @Convert(converter = BooleanToCharacterConverter.class)
    @Column(name = "RESOLVIDO", nullable = false)
    private Boolean resolvido = false;

    @Column(name = "DATA_ALERTA")
    private LocalDateTime dataAlerta;

    @Column(name = "DATA_RESOLUCAO")
    private LocalDateTime dataResolucao;
}
