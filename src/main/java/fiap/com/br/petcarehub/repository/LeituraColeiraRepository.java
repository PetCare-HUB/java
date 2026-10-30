package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.LeituraColeira;
import fiap.com.br.petcarehub.enums.StatusAtividade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeituraColeiraRepository extends JpaRepository<LeituraColeira, Long> {
    Optional<LeituraColeira> findTopByPetIdOrderByTimestampLeituraDesc(Long petId);
    List<LeituraColeira> findByPetIdAndTimestampLeituraBetweenOrderByTimestampLeituraDesc(Long petId, LocalDateTime de, LocalDateTime ate);
    List<LeituraColeira> findTop10ByPetIdOrderByTimestampLeituraDesc(Long petId);

    // O score precisa da PROPORÇÃO de sedentarismo na janela, não da última
    // leitura: um pico isolado não significa nada, e um pet parado a semana
    // inteira significa muito. Contar no banco evita trazer centenas de
    // leituras para a memória só para dividir dois números.
    long countByPetIdAndTimestampLeituraAfter(Long petId, LocalDateTime desde);

    long countByPetIdAndStatusAtividadeAndTimestampLeituraAfter(
            Long petId, StatusAtividade statusAtividade, LocalDateTime desde);
}
