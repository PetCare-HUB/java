package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.StatusAcesso;

import java.time.LocalDateTime;

public record TutorResponse(
        Long id,
        String nome,
        StatusAcesso statusAcesso,
        String email,
        String telefone,
        String cpf,
        LocalDateTime dataCadastro,
        ClinicaResumoResponse clinica
) {}
