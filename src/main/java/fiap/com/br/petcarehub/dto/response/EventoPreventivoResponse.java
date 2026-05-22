package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.TipoEventoPreventivo;

import java.time.LocalDate;

public record EventoPreventivoResponse(
        Long id,
        Long petId,
        String petNome,
        TipoEventoPreventivo tipo,
        String descricao,
        LocalDate dataPrevista,
        Boolean realizado
) {}
