package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.LeituraColeira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeituraColeiraRepository extends JpaRepository<LeituraColeira, Long> {
    Optional<LeituraColeira> findTopByPetIdOrderByTimestampLeituraDesc(Long petId);
    List<LeituraColeira> findByPetIdAndTimestampLeituraBetweenOrderByTimestampLeituraDesc(Long petId, LocalDateTime de, LocalDateTime ate);
    List<LeituraColeira> findTop10ByPetIdOrderByTimestampLeituraDesc(Long petId);
}
