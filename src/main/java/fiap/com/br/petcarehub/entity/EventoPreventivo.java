package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.StatusEventoPreventivo;
import fiap.com.br.petcarehub.enums.TipoEventoPreventivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "EVENTO_PREVENTIVO")
public class EventoPreventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENTO_PREVENTIVO")
    @SequenceGenerator(name = "SEQ_EVENTO_PREVENTIVO", sequenceName = "SEQ_EVENTO_PREVENTIVO", allocationSize = 1)
    @Column(name = "ID_EVENTO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROTOCOLO")
    private ProtocoloPreventivo protocolo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO", nullable = false, length = 30)
    private TipoEventoPreventivo tipo;

    @NotBlank
    @Size(max = 500)
    @Column(name = "DESCRICAO", nullable = false, length = 500)
    private String descricao;

    @NotNull
    @Column(name = "DATA_PREVISTA", nullable = false)
    private LocalDate dataPrevista;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusEventoPreventivo status;

    @Column(name = "DATA_REALIZACAO")
    private LocalDate dataRealizacao;
}
