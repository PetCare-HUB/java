package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.NivelAlerta;
import fiap.com.br.petcarehub.enums.TipoAlerta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertaSaudeResponse(
        Long id,
        Long petId,
        String petNome,
        TipoAlerta tipo,
        NivelAlerta nivel,
        String mensagem,
        BigDecimal valorDetectado,
        BigDecimal limiteReferencia,
        Boolean resolvido,
        LocalDateTime dataAlerta,
        LocalDateTime dataResolucao
) {}
