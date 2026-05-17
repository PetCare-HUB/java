package fiap.com.br.petcarehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "CLINICA")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clinica {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_clinica"
    )
    @SequenceGenerator(
            name = "seq_clinica",
            sequenceName = "SEQ_CLINICA",
            allocationSize = 1
    )
    @Column(name = "ID_CLINICA")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    @NotBlank
    @Size(max = 40)
    private String nome;

    @Column(name = "CNPJ", nullable = false, unique = true, length = 18)
    @NotBlank
    @Size(max = 14)
    private String cnpj;

    @Column(name = "EMAIL", length = 100)
    @Email
    @Size(max = 100)
    private String email;

    @Column(name = "TELEFONE", length = 20)
    @Size(max = 12)
    private String telefone;

    @Column(name = "ENDERECO", length = 255)
    private String endereco;

    @Column(name = "ATIVO")
    private Character ativo;

    @JsonIgnore
    @OneToMany(
            mappedBy = "clinica",
            cascade = CascadeType.ALL
    )
    private List<Pet> pets;

    @JsonIgnore
    @OneToMany(
            mappedBy = "clinica",
            cascade = CascadeType.ALL
    )
    private List<Consulta> consultas;
}