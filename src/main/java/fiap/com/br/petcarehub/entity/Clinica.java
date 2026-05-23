package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_CLINICA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_CLINICA_CNPJ", columnNames = "CNPJ")
})
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CLINICA")
    @SequenceGenerator(name = "SEQ_CLINICA", sequenceName = "SEQ_CLINICA", allocationSize = 1)
    @Column(name = "ID_CLINICA")
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "NOME", nullable = false, length = 120)
    private String nome;

    @NotBlank
    @Size(max = 18)
    @Column(name = "CNPJ", nullable = false, length = 18)
    private String cnpj;

    @NotBlank
    @Size(max = 200)
    @Column(name = "ENDERECO", length = 200)
    private String endereco;

    @Size(max = 20)
    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Builder.Default
    @OneToMany(mappedBy = "clinica")
    private List<Pet> pets = new ArrayList<>();
}
