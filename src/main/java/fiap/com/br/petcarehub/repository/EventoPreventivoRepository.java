package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.EventoPreventivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventoPreventivoRepository extends JpaRepository<EventoPreventivo, Long> {
    List<EventoPreventivo> findByPetIdOrderByDataPrevistaAsc(Long petId);

    List<EventoPreventivo> findByPetClinicaIdAndDataPrevistaBetweenOrderByDataPrevistaAsc(Long clinicaId, LocalDate inicio, LocalDate fim);

    long countByPetClinicaIdAndDataPrevistaBetween(Long clinicaId, LocalDate inicio, LocalDate fim);

    long countByPetClinicaIdAndDataPrevistaBetweenAndRealizadoFalse(Long clinicaId, LocalDate inicio, LocalDate fim);
}
