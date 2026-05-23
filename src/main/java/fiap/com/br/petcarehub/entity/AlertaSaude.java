package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "T_ALERTA_SAUDE")
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
    @Column(name = "TIPO", nullable = false, length = 40)
    private TipoAlerta tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL", nullable = false, length = 20)
    private NivelAlerta nivel;

    @NotBlank
    @Size(max = 500)
    @Column(name = "MENSAGEM", nullable = false, length = 500)
    private String mensagem;

    @Builder.Default
    @Column(name = "RESOLVIDO", nullable = false)
    private Boolean resolvido = false;

    @CreationTimestamp
    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "DATA_RESOLUCAO")
    private LocalDateTime dataResolucao;
}
