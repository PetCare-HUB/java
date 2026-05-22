package fiap.com.br.petcarehub.dto.request;

import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.TipoEventoPreventivo;
import jakarta.validation.constraints.*;

public record ProtocoloPreventivoRequest(
        @NotNull EspeciePet especie,
        @Size(max = 100) String raca,
        @NotNull TipoEventoPreventivo tipo,
        @NotBlank @Size(max = 150) String nome,
        @Size(max = 500) String descricao,
        @NotNull @Min(0) Integer idadeMesesAplicacao,
        @Min(1) Integer intervaloReforcoDias
) {}
