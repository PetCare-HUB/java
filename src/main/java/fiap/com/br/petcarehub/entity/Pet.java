package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Double pesoKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEXO")
    private SexoPet sexo;

    @Column(name = "CONDICOES_CRONICAS", length = 500)
    private String condicoesCronicas;

    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

    @Column(name = "ATIVO")
    private Boolean ativo;

    @OneToMany(mappedBy = "pet")
    private List<Consulta> consultas;
}