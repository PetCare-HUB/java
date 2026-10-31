package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.TipoAnaliseIa;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Registro de cada resposta da IA: o que foi pedido, o que o modelo respondeu,
 * qual modelo respondeu e de quais trechos da base veterinária ele partiu.
 *
 * <p>Guardar o histórico é o que permite auditar a IA depois — a resposta de
 * hoje pode ser comparada com os dados e o score que existiam naquele momento
 * ({@code score} aponta para o cálculo vigente quando a análise foi gerada).</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ANALISE_IA")
public class AnaliseIa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ANALISE_IA")
    @SequenceGenerator(name = "SEQ_ANALISE_IA", sequenceName = "SEQ_ANALISE_IA", allocationSize = 1)
    @Column(name = "ID_ANALISE")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PET", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SCORE")
    private ScoreSaude score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALERTA")
    private AlertaSaude alerta;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ANALISE", nullable = false, length = 30)
    private TipoAnaliseIa tipo;

    @Column(name = "PERGUNTA", length = 1000)
    private String pergunta;

    @Lob
    @Column(name = "RESPOSTA", nullable = false)
    private String resposta;

    @Column(name = "MODELO", length = 100)
    private String modelo;

    @Column(name = "FONTES", length = 1000)
    private String fontes;

    @CreationTimestamp
    @Column(name = "DATA_ANALISE", nullable = false)
    private LocalDateTime dataAnalise;
}
