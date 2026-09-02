package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.request.AtivarContaRequest;
import fiap.com.br.petcarehub.entity.Tutor;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.repository.TutorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Tutor ativarConta(AtivarContaRequest request) {

        Tutor tutor = tutorRepository
                .findByNomeAndCpfAndEmail(
                        request.nome(),
                        request.cpf(),
                        request.email()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dados do tutor não encontrados"
                ));
        if (tutor.getStatusAcesso() != StatusAcesso.PRE_CADASTRADO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tutor não está pendente de ativação"
            );
        }
        tutor.setSenha(
                passwordEncoder.encode(request.senha()));
        tutor.setStatusAcesso(StatusAcesso.ATIVO);
        return tutorRepository.save(tutor);
    }
}
