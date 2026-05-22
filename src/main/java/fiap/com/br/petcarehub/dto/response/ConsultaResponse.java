package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.TipoConsulta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        Long petId,
        String petNome,
        Long clinicaId,
        String clinicaNome,
        LocalDateTime dataConsulta,
        TipoConsulta tipo,
        String observacoes,
        BigDecimal valor
) {}
