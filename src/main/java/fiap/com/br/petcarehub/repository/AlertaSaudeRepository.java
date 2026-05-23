package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.AlertaSaude;
import fiap.com.br.petcarehub.enums.NivelAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertaSaudeRepository extends JpaRepository<AlertaSaude, Long>, JpaSpecificationExecutor<AlertaSaude> {

    List<AlertaSaude> findByPetIdAndResolvidoFalseOrderByDataCriacaoDesc(Long petId);

    List<AlertaSaude> findTop10ByPetIdOrderByDataCriacaoDesc(Long petId);

    long countByPetIdAndResolvidoFalseAndNivelIn(Long petId, List<NivelAlerta> niveis);

    long countByPetClinicaIdAndResolvidoFalse(Long clinicaId);

    @Query("SELECT a FROM AlertaSaude a WHERE a.pet.clinica.id = :clinicaId AND a.dataCriacao BETWEEN :inicio AND :fim ORDER BY a.dataCriacao DESC")
    List<AlertaSaude> findByPetClinicaIdAndDataAlertaBetweenOrderByDataAlertaDesc(
            @Param("clinicaId") Long clinicaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("SELECT COUNT(a) FROM AlertaSaude a WHERE a.pet.clinica.id = :clinicaId AND a.dataCriacao BETWEEN :inicio AND :fim")
    long countByPetClinicaIdAndDataAlertaBetween(
            @Param("clinicaId") Long clinicaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}