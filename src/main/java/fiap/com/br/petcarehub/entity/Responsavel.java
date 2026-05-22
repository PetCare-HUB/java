package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_RESPONSAVEL", uniqueConstraints = {
        @UniqueConstraint(name = "UK_RESPONSAVEL_EMAIL", columnNames = "EMAIL"),
        @UniqueConstraint(name = "UK_RESPONSAVEL_CPF", columnNames = "CPF")
})
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RESPONSAVEL")
    @SequenceGenerator(name = "SEQ_RESPONSAVEL", sequenceName = "SEQ_RESPONSAVEL", allocationSize = 1)
    @Column(name = "ID_RESPONSAVEL")
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "EMAIL", nullable = false, length = 150)
    private String email;

    @NotBlank
    @Size(max = 20)
    @Column(name = "TELEFONE", nullable = false, length = 20)
    private String telefone;

    @NotBlank
    @Size(min = 11, max = 14)
    @Column(name = "CPF", nullable = false, length = 14)
    private String cpf;

    @CreationTimestamp
    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @Builder.Default
    @OneToMany(mappedBy = "responsavel")
    private List<Pet> pets = new ArrayList<>();
}
