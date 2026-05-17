package fiap.com.br.petcarehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "RESPONSAVEL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Responsavel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_responsavel"
    )
    @SequenceGenerator(
            name = "seq_responsavel",
            sequenceName = "SEQ_RESPONSAVEL",
            allocationSize = 1
    )
    @Column(name = "ID_RESPONSAVEL")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    @NotBlank
    @Size(max = 50)
    private String nome;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Column(name = "TELEFONE", length = 20)
    @Size(max = 11)
    private String telefone;

    @Column(name = "CPF", nullable = false, unique = true, length = 14)
    @NotBlank
    @Size(max = 11)
    private String cpf;

    @CreationTimestamp
    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

    @Column(name = "ATIVO")
    private Character ativo;

    @JsonIgnore
    @OneToMany(mappedBy = "responsavel",
            cascade = CascadeType.ALL)
    private List<Pet> pets;
}