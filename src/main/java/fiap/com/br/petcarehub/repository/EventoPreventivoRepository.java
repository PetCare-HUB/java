package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.EventoPreventivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoPreventivoRepository extends JpaRepository<EventoPreventivo, Long> {
    List<EventoPreventivo> findByPetIdOrderByDataPrevistaAsc(Long petId);
}
