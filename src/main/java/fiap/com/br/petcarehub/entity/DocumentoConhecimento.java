package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Um trecho (chunk) da base de conhecimento veterinária, já com o embedding.
 *
 * <p>O Oracle 19c não tem tipo VECTOR, então o embedding fica como JSON num
 * CLOB e a similaridade é calculada em Java (ver ia/ConhecimentoVectorStore).
 * A base é pequena - uma coleção de .md -, então isso é suficiente.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DOCUMENTO_CONHECIMENTO")
public class DocumentoConhecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOCUMENTO_CONHECIMENTO")
    @SequenceGenerator(name = "SEQ_DOCUMENTO_CONHECIMENTO", sequenceName = "SEQ_DOCUMENTO_CONHECIMENTO", allocationSize = 1)
    @Column(name = "ID_DOCUMENTO")
    private Long id;

    /** Id do {@code Document} do Spring AI - é por ele que o VectorStore faz upsert e delete. */
    @Column(name = "DOC_ID", nullable = false, unique = true, length = 64)
    private String docId;

    /** Nome do arquivo .md de origem. */
    @Column(name = "FONTE", nullable = false, length = 200)
    private String fonte;

    @Column(name = "TITULO", length = 200)
    private String titulo;

    @Lob
    @Column(name = "CONTEUDO", nullable = false)
    private String conteudo;

    /** Metadata completa do Document, em JSON. */
    @Lob
    @Column(name = "METADADOS")
    private String metadados;

    /** Vetor do embedding serializado como JSON (array de float). */
    @Lob
    @Column(name = "EMBEDDING", nullable = false)
    private String embedding;

    @Column(name = "MODELO_EMBEDDING", nullable = false, length = 100)
    private String modeloEmbedding;

    @Column(name = "DIMENSOES", nullable = false)
    private Integer dimensoes;

    @CreationTimestamp
    @Column(name = "DATA_INDEXACAO", nullable = false, updatable = false)
    private LocalDateTime dataIndexacao;
}
