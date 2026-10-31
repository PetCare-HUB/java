package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.AnaliseIa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnaliseIaRepository extends JpaRepository<AnaliseIa, Long> {
    List<AnaliseIa> findTop20ByPetIdOrderByDataAnaliseDesc(Long petId);
}
