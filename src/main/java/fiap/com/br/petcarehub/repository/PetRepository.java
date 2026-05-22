package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Page<Pet> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Pet> findByEspecie(EspeciePet especie, Pageable pageable);

    Page<Pet> findByRacaContainingIgnoreCase(String raca, Pageable pageable);

    Page<Pet> findBySexo(SexoPet sexo, Pageable pageable);


    @Query("""
            SELECT p
            FROM Pet p
            JOIN p.clinica c
            WHERE c.id = :clinicaId
              AND p.scoreAtual <= :scoreMaximo
            """)
    Page<Pet> findPetsEmRisco(
            @Param("clinicaId") Long clinicaId,
            @Param("scoreMaximo") Integer scoreMaximo,
            Pageable pageable
    );
}
