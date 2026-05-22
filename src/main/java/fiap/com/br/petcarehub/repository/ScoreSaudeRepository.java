package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.ScoreSaude;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreSaudeRepository extends JpaRepository<ScoreSaude, Long> {
    Optional<ScoreSaude> findTopByPetIdOrderByDataCalculoDesc(Long petId);
    List<ScoreSaude> findTop10ByPetIdOrderByDataCalculoDesc(Long petId);
}
