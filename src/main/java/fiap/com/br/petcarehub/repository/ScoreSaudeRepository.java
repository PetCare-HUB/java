package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.ScoreSaude;
import fiap.com.br.petcarehub.enums.CategoriaScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScoreSaudeRepository extends JpaRepository<ScoreSaude, Long> {
    Optional<ScoreSaude> findTopByPetIdOrderByDataCalculoDesc(Long petId);
    List<ScoreSaude> findTop10ByPetIdOrderByDataCalculoDesc(Long petId);

    List<ScoreSaude> findByPetClinicaIdAndDataCalculoBetween(Long clinicaId, LocalDateTime inicio, LocalDateTime fim);

    long countByPetClinicaIdAndDataCalculoBetween(Long clinicaId, LocalDateTime inicio, LocalDateTime fim);

    long countByPetClinicaIdAndDataCalculoBetweenAndCategoria(Long clinicaId, LocalDateTime inicio, LocalDateTime fim, CategoriaScore categoria);
}
