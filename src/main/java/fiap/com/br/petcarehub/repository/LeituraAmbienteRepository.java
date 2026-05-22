package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.LeituraAmbiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeituraAmbienteRepository extends JpaRepository<LeituraAmbiente, Long> {
    Optional<LeituraAmbiente> findTopByPetIdOrderByTimestampLeituraDesc(Long petId);
    List<LeituraAmbiente> findByPetIdAndTimestampLeituraBetweenOrderByTimestampLeituraDesc(Long petId, LocalDateTime de, LocalDateTime ate);
    List<LeituraAmbiente> findTop10ByPetIdOrderByTimestampLeituraDesc(Long petId);
}
