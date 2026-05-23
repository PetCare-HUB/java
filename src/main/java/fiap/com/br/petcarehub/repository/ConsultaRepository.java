package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Consulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Page<Consulta> findByPetId(Long petId, Pageable pageable);
    List<Consulta> findTop10ByPetIdOrderByDataConsultaDesc(Long petId);

    List<Consulta> findByClinicaIdAndDataConsultaBetweenOrderByDataConsultaAsc(Long clinicaId, LocalDateTime inicio, LocalDateTime fim);

    long countByClinicaIdAndDataConsultaBetween(Long clinicaId, LocalDateTime inicio, LocalDateTime fim);
}
