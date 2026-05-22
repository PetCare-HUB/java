package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.StatusAtividade;

import java.time.LocalDateTime;

public record LeituraColeiraResponse(
        Long id,
        Long petId,
        String petNome,
        StatusAtividade statusAtividade,
        Integer nivelBateria,
        LocalDateTime timestampLeitura
) {}
