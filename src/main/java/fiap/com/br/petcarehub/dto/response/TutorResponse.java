package fiap.com.br.petcarehub.dto.response;

import fiap.com.br.petcarehub.enums.Role;
import fiap.com.br.petcarehub.enums.StatusAcesso;

import java.time.LocalDateTime;

public record TutorResponse(
        Long id,
        String nome,
        StatusAcesso statusAcesso,
        Role role ,
        String email,
        String telefone,
        String cpf,
        LocalDateTime dataCadastro
) {}
