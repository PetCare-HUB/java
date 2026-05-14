package fiap.com.br.petcarehub.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLINICA")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "CNPJ", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "ENDERECO", length = 255)
    private String endereco;

    @Column(name = "ATIVO")
    private Boolean ativo;

    @OneToMany(mappedBy = "clinica")
    private List<Pet> pets;

    @OneToMany(mappedBy = "clinica")
    private List<Consulta> consultas;
}