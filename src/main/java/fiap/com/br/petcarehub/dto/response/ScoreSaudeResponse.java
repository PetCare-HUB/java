package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.CategoriaScore;

import java.time.LocalDateTime;

public record ScoreSaudeResponse(
        Long id,
        Long petId,
        String petNome,
        Integer score,
        CategoriaScore categoria,
        String observacao,
        LocalDateTime dataCalculo
) {}
