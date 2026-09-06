package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.ScoreSaude;
import fiap.com.br.petcarehub.enums.CategoriaScore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScoreSaudeRepository extends JpaRepository<ScoreSaude, Long> {

    Optional<ScoreSaude> findTopByPetIdOrderByDataCalculoDesc(Long petId);

    List<ScoreSaude> findTop10ByPetIdOrderByDataCalculoDesc(Long petId);

    List<ScoreSaude> findByPetClinicaIdAndDataCalculoBetween(
            Long clinicaId,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    long countByPetClinicaIdAndDataCalculoBetween(
            Long clinicaId,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    long countByPetClinicaIdAndDataCalculoBetweenAndCategoria(
            Long clinicaId,
            LocalDateTime inicio,
            LocalDateTime fim,
            CategoriaScore categoria
    );

    @Query("""
        SELECT s
        FROM ScoreSaude s
        WHERE s.pet.clinica.id = :clinicaId
          AND s.pet.ativo = true
          AND s.scoreTotal >= :score
          AND NOT EXISTS (
              SELECT s2
              FROM ScoreSaude s2
              WHERE s2.pet.id = s.pet.id
                AND s2.dataCalculo > s.dataCalculo
          )
        ORDER BY s.scoreTotal ASC
        """)
    List<ScoreSaude> findUltimosScoresPorClinicaEMaiorOuIgual(
            @Param("clinicaId") Long clinicaId,
            @Param("score") Integer score
    );

    @Query("""
        SELECT s
        FROM ScoreSaude s
        WHERE s.pet.clinica.id = :clinicaId
          AND s.pet.ativo = true
          AND s.scoreTotal BETWEEN :scoreMin AND :scoreMax
          AND NOT EXISTS (
              SELECT s2
              FROM ScoreSaude s2
              WHERE s2.pet.id = s.pet.id
                AND s2.dataCalculo > s.dataCalculo
          )
        ORDER BY s.scoreTotal ASC
        """)
    List<ScoreSaude> findUltimosScoresPorClinicaEntre(
            @Param("clinicaId") Long clinicaId,
            @Param("scoreMin") Integer scoreMin,
            @Param("scoreMax") Integer scoreMax
    );

    @Query("""
        SELECT s
        FROM ScoreSaude s
        WHERE s.pet.clinica.id = :clinicaId
          AND s.pet.ativo = true
          AND s.scoreTotal < :score
          AND NOT EXISTS (
              SELECT s2
              FROM ScoreSaude s2
              WHERE s2.pet.id = s.pet.id
                AND s2.dataCalculo > s.dataCalculo
          )
        ORDER BY s.scoreTotal ASC
        """)
    List<ScoreSaude> findUltimosScoresPorClinicaMenorQue(
            @Param("clinicaId") Long clinicaId,
            @Param("score") Integer score
    );

    @Query("""
        SELECT COUNT(s)
        FROM ScoreSaude s
        WHERE s.pet.clinica.id = :clinicaId
          AND s.pet.ativo = true
          AND s.scoreTotal < :score
          AND NOT EXISTS (
              SELECT s2
              FROM ScoreSaude s2
              WHERE s2.pet.id = s.pet.id
                AND s2.dataCalculo > s.dataCalculo
          )
        """)
    long countPetsComUltimoScoreMenorQue(
            @Param("clinicaId") Long clinicaId,
            @Param("score") Integer score
    );

    @Query("""
    SELECT s
    FROM ScoreSaude s
    WHERE s.pet.clinica.id = :clinicaId
      AND s.pet.ativo = true
      AND s.scoreTotal <= :scoreMaximo
      AND NOT EXISTS (
          SELECT s2
          FROM ScoreSaude s2
          WHERE s2.pet.id = s.pet.id
            AND s2.dataCalculo > s.dataCalculo
      )
    ORDER BY s.scoreTotal ASC
    """)
    Page<ScoreSaude> findPetsEmRisco(
            @Param("clinicaId") Long clinicaId,
            @Param("scoreMaximo") Integer scoreMaximo,
            Pageable pageable
    );
}