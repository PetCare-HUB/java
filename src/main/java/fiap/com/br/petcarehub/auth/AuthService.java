package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.request.AtivarContaRequest;
import fiap.com.br.petcarehub.entity.Tutor;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import fiap.com.br.petcarehub.repository.TutorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClinicaRepository clinicaRepository;

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
                        "Não encontramos um cadastro elegível para acesso ao PetCare Hub. Entre em contato com sua clínica veterinária parceira."
                ));
        if (tutor.getStatusAcesso() != StatusAcesso.PRE_CADASTRADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este cadastro já está ativo ou não está disponível para ativação."
            );
        }
        tutor.setSenha(
                passwordEncoder.encode(request.senha())
        );
        tutor.setStatusAcesso(StatusAcesso.ATIVO);
        return tutorRepository.save(tutor);
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        var tutor = tutorRepository.findByEmail(email);

        if (tutor.isPresent()) {
            return User
                    .withUsername(tutor.get().getEmail())
                    .password(tutor.get().getSenha())
                    .roles("TUTOR")
                    .build();
        }

        var clinica = clinicaRepository.findByEmail(email);

        if (clinica.isPresent()) {
            return User
                    .withUsername(clinica.get().getEmail())
                    .password(clinica.get().getSenha())
                    .roles("CLINICA")
                    .build();
        }

        throw new UsernameNotFoundException(
                "Usuário não encontrado: " + email
        );

    }

}
