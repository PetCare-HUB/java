package fiap.com.br.petcarehub.dto.request;

import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;
import fiap.com.br.petcarehub.validation.MaxAge;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetRequest(
        @NotBlank @Size(max = 80) String nome,
        @NotNull EspeciePet especie,
        @Size(max = 80) String raca,
        @PastOrPresent @MaxAge(25) LocalDate dataNascimento,
        @NotNull @Positive BigDecimal pesoKg,
        SexoPet sexo,
        @Size(max = 300) String condicoesCronicas,
        Boolean ativo,
        @NotNull Long tutorId,
        @NotNull Long clinicaId
) {}