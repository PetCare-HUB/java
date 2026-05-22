package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Consulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    Page<Consulta> findByPetId(Long petId, Pageable pageable);
    List<Consulta> findTop10ByPetIdOrderByDataConsultaDesc(Long petId);
}
