package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.LeituraComedouro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeituraComedouroRepository extends JpaRepository<LeituraComedouro, Long> {
    Optional<LeituraComedouro> findTopByPetIdOrderByTimestampLeituraDesc(Long petId);
    List<LeituraComedouro> findByPetIdAndTimestampLeituraBetweenOrderByTimestampLeituraDesc(Long petId, LocalDateTime de, LocalDateTime ate);
    List<LeituraComedouro> findTop10ByPetIdOrderByTimestampLeituraDesc(Long petId);
}
