package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.config.BooleanToCharacterConverter;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PET")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PET")
    @SequenceGenerator(name = "SEQ_PET", sequenceName = "SEQ_PET", allocationSize = 1)
    @Column(name = "ID_PET")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "NOME", nullable = false, length = 80)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESPECIE", nullable = false, length = 20)
    private EspeciePet especie;

    @Size(max = 100)
    @Column(name = "RACA", length = 80)
    private String raca;

    @PastOrPresent
    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Positive
    @Column(name = "PESO_KG", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEXO", length = 1)
    private SexoPet sexo;

    @Size(max = 500)
    @Column(name = "CONDICOES_CRONICAS", length = 300)
    private String condicoesCronicas;

    @NotNull
    @Builder.Default
    @Convert(converter = BooleanToCharacterConverter.class)
    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;


    @CreationTimestamp
    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TUTOR", nullable = false)
    private Tutor tutor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLINICA", nullable = false)
    private Clinica clinica;

    @Builder.Default
    @OneToMany(mappedBy = "pet")
    private List<Consulta> consultas = new ArrayList<>();
}
