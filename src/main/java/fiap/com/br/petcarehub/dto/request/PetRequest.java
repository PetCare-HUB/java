package fiap.com.br.petcarehub.dto.request;

import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import fiap.com.br.petcarehub.validation.MaxAge;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotNull EspeciePet especie,
        @Size(max = 100) String raca,
        @PastOrPresent @MaxAge(25) LocalDate dataNascimento,
        @Positive BigDecimal pesoKg,
        SexoPet sexo,
        @Size(max = 500) String condicoesCronicas,
        Boolean ativo,
        @NotNull Long responsavelId,
        @NotNull Long clinicaId
) {}