package fiap.com.br.petcarehub.entity;

import fiap.com.br.petcarehub.enums.StatusAcesso;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "CLINICA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_CLINICA_CNPJ", columnNames = "CNPJ"),
        @UniqueConstraint(name = "UK_CLINICA_EMAIL", columnNames = "EMAIL")
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
    @Email
    @Size(max = 150)
    @Column(name = "EMAIL", nullable = false, length = 150)
    private String email;

    @Column(name = "senha_hash", length = 255)
    private String senha;

    // Se a clinica pode autenticar. PRE_CADASTRADO = o registro existe
    // (auto-cadastro ou pre-cadastro vindo do .NET) mas ainda nao foi
    // aprovado por um admin da PetCare Hub. Separado de 'ativo', que e
    // a flag operacional de negocio usada tambem pelo .NET.
    @Enumerated(EnumType.STRING)
    @Column(name = "status_acesso", nullable = false)
    private StatusAcesso statusAcesso;

    // Guardado sempre normalizado (so digitos). A coluna no Oracle e
    // VARCHAR2(14), entao CNPJ formatado estouraria ORA-12899 - quem
    // recebe do cliente normaliza antes de montar a entidade.
    @NotBlank
    @Size(max = 14)
    @Column(name = "CNPJ", nullable = false, length = 14)
    private String cnpj;

    @NotBlank
    @Size(max = 200)
    @Column(name = "ENDERECO", length = 200)
    private String endereco;

    @Size(max = 20)
    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    private char ativo;

    @Builder.Default
    @OneToMany(mappedBy = "clinica")
    private List<Pet> pets = new ArrayList<>();
}
