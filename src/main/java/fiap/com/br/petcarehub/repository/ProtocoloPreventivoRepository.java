package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.ProtocoloPreventivo;
import fiap.com.br.petcarehub.enums.EspeciePet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProtocoloPreventivoRepository extends JpaRepository<ProtocoloPreventivo, Long> {
    List<ProtocoloPreventivo> findByEspecieOrderByIdadeMesesAplicacaoAsc(EspeciePet especie);
}
