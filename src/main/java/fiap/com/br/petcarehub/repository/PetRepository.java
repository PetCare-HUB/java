package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    Page<Pet> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    long countByClinicaId(Long clinicaId);

    long countByClinicaIdAndAtivoTrue(Long clinicaId);

    long countByClinicaIdAndScoreAtualLessThanEqual(Long clinicaId, Integer scoreAtual);

    @Query("SELECT p FROM Pet p WHERE p.clinica.id = :clinicaId AND p.scoreAtual < :score AND p.ativo = true ORDER BY p.scoreAtual ASC")
    List<Pet> findByClinicaIdAndScoreAtualLessThanAndAtivoTrueOrderByScoreAtualAsc(
            @Param("clinicaId") Long clinicaId,
            @Param("score") Integer score
    );

    long countByClinicaIdAndScoreAtualLessThan(Long clinicaId, Integer score);

    @Query("SELECT p FROM Pet p WHERE p.clinica.id = :clinicaId AND p.scoreAtual >= :score AND p.ativo = true ORDER BY p.scoreAtual ASC")
    List<Pet> findByClinicaIdAndScoreAtualGreaterThanEqualAndAtivoTrueOrderByScoreAtualAsc(
            @Param("clinicaId") Long clinicaId,
            @Param("score") Integer score
    );

    @Query("SELECT p FROM Pet p WHERE p.clinica.id = :clinicaId AND p.scoreAtual BETWEEN :scoreMin AND :scoreMax AND p.ativo = true ORDER BY p.scoreAtual ASC")
    List<Pet> findByClinicaIdAndScoreAtualBetweenAndAtivoTrueOrderByScoreAtualAsc(
            @Param("clinicaId") Long clinicaId,
            @Param("scoreMin") Integer scoreMin,
            @Param("scoreMax") Integer scoreMax
    );

    @Query("""
            SELECT p
            FROM Pet p
            WHERE p.clinica.id = :clinicaId
              AND p.scoreAtual <= :scoreMaximo
            """)
    Page<Pet> findPetsEmRisco(
            @Param("clinicaId") Long clinicaId,
            @Param("scoreMaximo") Integer scoreMaximo,
            Pageable pageable
    );
}