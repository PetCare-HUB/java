package fiap.com.br.petcarehub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PetResponseDTO {

    private Long id;
    private String nome;
    private String especie;
    private BigDecimal pesoKg;

    private ResponsavelResumoDTO responsavel;
    private ClinicaResumoDTO clinica;
}