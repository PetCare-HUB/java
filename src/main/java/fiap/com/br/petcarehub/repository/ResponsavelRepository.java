package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.projection.ResponsavelSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResponsavelRepository
        extends JpaRepository<Responsavel, Long> {

    @Query("""
            SELECT
                r.id AS id,
                r.nome AS nome,
                r.email AS email,
                r.cpf AS cpf,
                r.telefone AS telefone
            FROM Responsavel r
            WHERE LOWER(r.nome)
            LIKE LOWER(CONCAT('%', :nome, '%'))
            """)
    Page<ResponsavelSummary> findByNomeContaining(
            @Param("nome") String nome,
            Pageable pageable
    );

    @Query("""
            SELECT
                r.id AS id,
                r.nome AS nome,
                r.email AS email,
                r.cpf AS cpf,
                r.telefone AS telefone
            FROM Responsavel r
            WHERE LOWER(r.email)
            LIKE LOWER(CONCAT('%', :email, '%'))
            """)
    Page<ResponsavelSummary> findByEmailContaining(
            @Param("email") String email,
            Pageable pageable
    );

    @Query("""
            SELECT
                r.id AS id,
                r.nome AS nome,
                r.email AS email,
                r.cpf AS cpf,
                r.telefone AS telefone
            FROM Responsavel r
            WHERE r.cpf = :cpf
            """)
    Page<ResponsavelSummary> findByCpf(
            @Param("cpf") String cpf,
            Pageable pageable
    );

    @Query("""
            SELECT
                r.id AS id,
                r.nome AS nome,
                r.email AS email,
                r.cpf AS cpf,
                r.telefone AS telefone
            FROM Responsavel r
            WHERE r.telefone
            LIKE CONCAT('%', :telefone, '%')
            """)
    Page<ResponsavelSummary> findByTelefoneContaining(
            @Param("telefone") String telefone,
            Pageable pageable
    );
}