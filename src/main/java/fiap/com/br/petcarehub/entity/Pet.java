package fiap.com.br.petcarehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PET")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_pet"
    )
    @SequenceGenerator(
            name = "seq_pet",
            sequenceName = "SEQ_PET",
            allocationSize = 1
    )
    @Column(name = "ID_PET")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "ID_RESPONSAVEL", nullable = false)
    private Responsavel responsavel;

    @ManyToOne
    @JoinColumn(name = "ID_CLINICA", nullable = false)
    private Clinica clinica;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIE", nullable = false)
    private EspeciePet especie;

    @Column(name = "RACA", length = 100)
    private String raca;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Column(name = "PESO_KG")
    private BigDecimal pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEXO", columnDefinition = "CHAR(1)")
    private SexoPet sexo;

    @Column(name = "CONDICOES_CRONICAS", length = 500)
    private String condicoesCronicas;

    @CreationTimestamp
    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

    @Column(name = "ATIVO")
    private Character ativo;

    @JsonIgnore
    @OneToMany(mappedBy = "pet",
            cascade = CascadeType.ALL)
    private List<Consulta> consultas;
}