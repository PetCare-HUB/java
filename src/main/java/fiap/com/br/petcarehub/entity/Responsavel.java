package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESPONSAVEL")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "CPF", nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "DATA_CADASTRO")
    private LocalDate dataCadastro;

    @Column(name = "ATIVO")
    private Character ativo;

    @OneToMany(mappedBy = "responsavel")
    private List<Pet> pets;
}