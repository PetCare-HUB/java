package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.AlertaSaude;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertaSaudeRepository extends JpaRepository<AlertaSaude, Long> {
    List<AlertaSaude> findByPetIdAndResolvidoFalseOrderByDataCriacaoDesc(Long petId);
    List<AlertaSaude> findTop10ByPetIdOrderByDataCriacaoDesc(Long petId);
    long countByPetIdAndResolvidoFalseAndNivelIn(Long petId, List<NivelAlerta> niveis);

    @Query("""
            SELECT a
            FROM AlertaSaude a
            JOIN a.pet p
            WHERE (:petId IS NULL OR p.id = :petId)
              AND (:tipo IS NULL OR a.tipo = :tipo)
              AND (:resolvido IS NULL OR a.resolvido = :resolvido)
            """)
    Page<AlertaSaude> buscar(
            @Param("petId") Long petId,
            @Param("tipo") TipoAlerta tipo,
            @Param("resolvido") Boolean resolvido,
            Pageable pageable
    );
}
