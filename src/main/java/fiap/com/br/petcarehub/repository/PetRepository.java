package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import fiap.com.br.petcarehub.projection.PetSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Page<PetSummary> findByNomeContainingIgnoreCase(
            String nome,
            Pageable pageable
    );

    @Query("""
            SELECT
                p.id AS id,
                p.nome AS nome,
                p.especie AS especie,
                p.raca AS raca,
                p.pesoKg AS pesoKg,
                p.sexo AS sexo,
                p.ativo AS ativo,

                r.id AS responsavelId,
                r.nome AS responsavelNome,

                c.id AS clinicaId,
                c.nome AS clinicaNome

            FROM Pet p

            JOIN p.responsavel r
            JOIN p.clinica c

            WHERE p.especie = :especie
            """)
    Page<PetSummary> findByEspecie(
            @Param("especie") EspeciePet especie,
            Pageable pageable
    );

    @Query("""
            SELECT
                p.id AS id,
                p.nome AS nome,
                p.especie AS especie,
                p.raca AS raca,
                p.pesoKg AS pesoKg,
                p.sexo AS sexo,
                p.ativo AS ativo,

                r.id AS responsavelId,
                r.nome AS responsavelNome,

                c.id AS clinicaId,
                c.nome AS clinicaNome

            FROM Pet p

            JOIN p.responsavel r
            JOIN p.clinica c

            WHERE LOWER(p.raca)
            LIKE LOWER(CONCAT('%', :raca, '%'))
            """)
    Page<PetSummary> findByRacaContaining(
            @Param("raca") String raca,
            Pageable pageable
    );

    @Query("""
            SELECT
                p.id AS id,
                p.nome AS nome,
                p.especie AS especie,
                p.raca AS raca,
                p.pesoKg AS pesoKg,
                p.sexo AS sexo,
                p.ativo AS ativo,

                r.id AS responsavelId,
                r.nome AS responsavelNome,

                c.id AS clinicaId,
                c.nome AS clinicaNome

            FROM Pet p

            JOIN p.responsavel r
            JOIN p.clinica c

            WHERE p.sexo = :sexo
            """)
    Page<PetSummary> findBySexo(
            @Param("sexo") SexoPet sexo,
            Pageable pageable
    );
}