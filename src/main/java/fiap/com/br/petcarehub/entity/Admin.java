package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Admin da PetCare Hub - quem decide quais clinicas entram na rede.
 *
 * Nao e uma Clinica nem um Tutor (nao tem pets, nao tem CNPJ, nao
 * pertence a lugar nenhum da hierarquia), por isso tem tabela
 * propria em vez de uma flag em CLINICA.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ADMIN_CLYVO", uniqueConstraints = {
        @UniqueConstraint(name = "UK_ADMIN_CLYVO_EMAIL", columnNames = "EMAIL")
})
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ADMIN_CLYVO")
    @SequenceGenerator(name = "SEQ_ADMIN_CLYVO", sequenceName = "SEQ_ADMIN_CLYVO", allocationSize = 1)
    @Column(name = "ID_ADMIN")
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

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senha;

    @Column(name = "ATIVO", nullable = false)
    private char ativo;
}
