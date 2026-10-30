package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Consulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Page<Consulta> findByPetId(Long petId, Pageable pageable);
    List<Consulta> findTop10ByPetIdOrderByDataConsultaDesc(Long petId);

    // O score precisa só da última: buscar 10 linhas para usar uma é
    // trabalho jogado fora a cada recálculo.
    Optional<Consulta> findTopByPetIdOrderByDataConsultaDesc(Long petId);

    List<Consulta> findByClinicaIdAndDataConsultaBetweenOrderByDataConsultaAsc(Long clinicaId, LocalDateTime inicio, LocalDateTime fim);

    long countByClinicaIdAndDataConsultaBetween(Long clinicaId, LocalDateTime inicio, LocalDateTime fim);
}
