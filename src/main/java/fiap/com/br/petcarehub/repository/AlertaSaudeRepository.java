package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.AlertaSaude;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlertaSaudeRepository extends JpaRepository<AlertaSaude, Long>, JpaSpecificationExecutor<AlertaSaude> {

    List<AlertaSaude> findByPetIdAndResolvidoFalseOrderByDataAlertaDesc(Long petId);

    // Alerta aberto do mesmo tipo para o mesmo pet: usado para atualizar em vez
    // de empilhar duplicatas. "Pet sedentário" é UM fato, não um por leitura.
    Optional<AlertaSaude> findTopByPetIdAndTipoAndResolvidoFalseOrderByDataAlertaDesc(
            Long petId, TipoAlerta tipo);

    List<AlertaSaude> findTop10ByPetIdOrderByDataAlertaDesc(Long petId);


    long countByPetIdAndResolvidoFalseAndNivelIn(Long petId, List<NivelAlerta> niveis);

    long countByPetClinicaIdAndResolvidoFalse(Long clinicaId);

    @Query("SELECT a FROM AlertaSaude a WHERE a.pet.clinica.id = :clinicaId AND a.dataAlerta BETWEEN :inicio AND :fim ORDER BY a.dataAlerta DESC")
    List<AlertaSaude> findByPetClinicaIdAndDataAlertaBetweenOrderByDataAlertaDesc(
            @Param("clinicaId") Long clinicaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("SELECT COUNT(a) FROM AlertaSaude a WHERE a.pet.clinica.id = :clinicaId AND a.dataAlerta BETWEEN :inicio AND :fim")
    long countByPetClinicaIdAndDataAlertaBetween(
            @Param("clinicaId") Long clinicaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}