package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.TipoEventoPreventivo;

public record ProtocoloPreventivoResponse(
        Long id,
        EspeciePet especie,
        String raca,
        TipoEventoPreventivo tipo,
        String descricao,
        Integer idadeMesesRecomendada,
        Integer intervaloDias,
        char ativo
) {}
