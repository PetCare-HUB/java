package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.enums.SexoPet;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PetResponse(
        Long id,
        String nome,
        EspeciePet especie,
        String raca,
        LocalDate dataNascimento,
        BigDecimal pesoKg,
        SexoPet sexo,
        String condicoesCronicas,
        Boolean ativo,
        Integer scoreAtual,
        LocalDateTime dataCadastro,
        ResponsavelResumoResponse responsavel,
        ClinicaResumoResponse clinica
) {}
