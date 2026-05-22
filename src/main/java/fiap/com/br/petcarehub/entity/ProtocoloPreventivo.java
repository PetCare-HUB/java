package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.TipoEventoPreventivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_PROTOCOLO_PREVENTIVO")
public class ProtocoloPreventivo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PROTOCOLO_PREVENTIVO")
    @SequenceGenerator(name = "SEQ_PROTOCOLO_PREVENTIVO", sequenceName = "SEQ_PROTOCOLO_PREVENTIVO", allocationSize = 1)
    @Column(name = "ID_PROTOCOLO")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIE", nullable = false, length = 20)
    private EspeciePet especie;

    @Size(max = 100)
    @Column(name = "RACA", length = 100)
    private String raca;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 30)
    private TipoEventoPreventivo tipo;

    @NotBlank
    @Size(max = 150)
    @Column(name = "NOME", nullable = false, length = 150)
    private String nome;

    @Size(max = 500)
    @Column(name = "DESCRICAO", length = 500)
    private String descricao;

    @NotNull
    @Min(0)
    @Column(name = "IDADE_MESES_APLICACAO", nullable = false)
    private Integer idadeMesesAplicacao;

    @Min(1)
    @Column(name = "INTERVALO_REFORCO_DIAS")
    private Integer intervaloReforcoDias;
}
