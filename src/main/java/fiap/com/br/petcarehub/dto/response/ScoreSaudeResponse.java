package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.CategoriaScore;

import java.time.LocalDateTime;

public record ScoreSaudeResponse(
        Long id,
        Long petId,
        String petNome,
        Integer scoreTotal,
        Integer scoreAtividade,
        Integer scoreAlimentacao,
        Integer scoreAmbiente,
        Integer scoreConsulta,
        Integer scorePreventivo,
        CategoriaScore categoria,
        LocalDateTime dataCalculo
) {}
