package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.request.AtivarContaRequest;
import fiap.com.br.petcarehub.dto.request.AtivarClinicaRequest;
import fiap.com.br.petcarehub.dto.request.CadastroClinicaRequest;
import fiap.com.br.petcarehub.dto.response.LoginResponse;
import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.entity.Admin;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.entity.Tutor;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.repository.AdminRepository;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import fiap.com.br.petcarehub.repository.TutorRepository;
import fiap.com.br.petcarehub.service.DtoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClinicaRepository clinicaRepository;
    private final AdminRepository adminRepository;
    private final TokenService tokenService;

    @Value("${app.auth.clinic-activation-code:}")
    private String clinicActivationCode;

    /**
     * Solicitacao publica de credenciamento. A clinica ja escolhe a senha
     * aqui, mas nasce PRE_CADASTRADO e NAO consegue logar: quem libera e um
     * admin da PetCare Hub (PUT /admin/clinicas/{id}/ativar).
     *
     * Por isso o endpoint pode ser publico sem abrir brecha - o registro
     * criado nao da acesso a nada ate a aprovacao.
     */
    @Transactional
    public ClinicaResponse cadastrarClinica(CadastroClinicaRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String cnpj = normalizarCnpj(request.cnpj());
        if (clinicaRepository.findByEmail(email).isPresent() || clinicaRepository.existsByCnpj(cnpj)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe uma clínica com este e-mail ou CNPJ. Use a ativação de clínica ou entre em contato com o suporte.");
        }

        Clinica clinica = Clinica.builder()
                .nome(request.nome().trim())
                .email(email)
                .senha(passwordEncoder.encode(request.senha()))
                .cnpj(cnpj)
                .endereco(request.endereco().trim())
                .telefone(request.telefone())
                .ativo('S')
                .statusAcesso(StatusAcesso.PRE_CADASTRADO)
                .build();
        return DtoMapper.toResponse(clinicaRepository.save(clinica));
    }

    /**
     * Caminho das clinicas pre-cadastradas pelo .NET (que nascem sem senha):
     * a clinica define a senha provando posse do codigo de credenciamento
     * que a PetCare Hub entregou a ela.
     *
     * Aqui nao ha aprovacao posterior do admin de proposito - ter o codigo
     * JA e a autorizacao do admin. Seriam dois portoes para a mesma decisao.
     */
    @Transactional
    public LoginResponse ativarClinica(AtivarClinicaRequest request) {
        if (clinicActivationCode == null || clinicActivationCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A ativação de clínicas existentes não está habilitada neste ambiente.");
        }
        if (!MessageDigest.isEqual(clinicActivationCode.getBytes(StandardCharsets.UTF_8),
                request.codigoAtivacao().getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Código de ativação inválido.");
        }

        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String cnpj = normalizarCnpj(request.cnpj());
        Clinica clinica = clinicaRepository.findByEmail(email)
                .filter(c -> normalizarCnpj(c.getCnpj()).equals(cnpj))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Clínica não encontrada com esses dados."));
        if (clinica.getSenha() != null && !clinica.getSenha().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta clínica já possui acesso ativo.");
        }
        if (clinica.getAtivo() != 'S') {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta clínica está inativa.");
        }

        clinica.setSenha(passwordEncoder.encode(request.senha()));
        clinica.setStatusAcesso(StatusAcesso.ATIVO);
        clinicaRepository.save(clinica);
        UserDetails userDetails = User.withUsername(clinica.getEmail())
                .password(clinica.getSenha()).roles("CLINICA").build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        return tokenService.generateToken(authentication);
    }

    private static String normalizarCnpj(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }

    @Transactional
    public LoginResponse ativarConta(AtivarContaRequest request) {
        Tutor tutor = tutorRepository.findByNomeAndCpfAndEmail(
                request.nome(),
                request.cpf(),
                request.email()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Não encontramos um cadastro elegível para acesso ao PetCare Hub. Entre em contato com sua clínica veterinária parceira."));
        if (tutor.getStatusAcesso() != StatusAcesso.PRE_CADASTRADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este cadastro já está ativo ou não está disponível para ativação.");
        }

        tutor.setSenha(passwordEncoder.encode(request.senha()));
        tutor.setStatusAcesso(StatusAcesso.ATIVO);

        Tutor tutorSalvo = tutorRepository.save(tutor);

        UserDetails userDetails = User.withUsername(tutorSalvo.getEmail()).password(tutorSalvo.getSenha()).authorities(new SimpleGrantedAuthority("ROLE_TUTOR")).build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        return tokenService.generateToken(authentication);
    }


    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        var tutor = tutorRepository.findByEmail(email);

        if (tutor.isPresent()) {

            var tutorEncontrado = tutor.get();

            if (tutorEncontrado.getStatusAcesso() == StatusAcesso.PRE_CADASTRADO) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cadastro pendente de ativação. Use /auth/ativar-conta."
                );
            }

            if (tutorEncontrado.getStatusAcesso() == StatusAcesso.BLOQUEADO) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Acesso bloqueado. Entre em contato com sua clínica."
                );
            }

            if (tutorEncontrado.getStatusAcesso() == StatusAcesso.INATIVO) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Este vínculo foi encerrado."
                );
            }

            return User
                    .withUsername(tutorEncontrado.getEmail())
                    .password(tutorEncontrado.getSenha())
                    .roles("TUTOR")
                    .build();
        }

        var clinica = clinicaRepository.findByEmail(email);

        if (clinica.isPresent()) {
            var clinicaEncontrada = clinica.get();

            if (clinicaEncontrada.getSenha() == null || clinicaEncontrada.getSenha().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Clínica sem senha cadastrada. Use /auth/ativar-clinica.");
            }

            // Este e o portao do credenciamento: a clinica ja tem senha, mas
            // so entra depois que um admin da PetCare Hub aprovou.
            if (clinicaEncontrada.getStatusAcesso() == StatusAcesso.PRE_CADASTRADO) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Cadastro em análise pela PetCare Hub. Você será avisado quando a clínica for aprovada.");
            }

            if (clinicaEncontrada.getStatusAcesso() == StatusAcesso.BLOQUEADO) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Acesso bloqueado. Entre em contato com a PetCare Hub.");
            }

            if (clinicaEncontrada.getStatusAcesso() == StatusAcesso.INATIVO) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Este credenciamento foi encerrado.");
            }

            // 'ativo' e a flag operacional que o .NET tambem controla -
            // desativar a clinica por la tambem tira o acesso dela aqui.
            if (clinicaEncontrada.getAtivo() != 'S') {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Esta clínica está inativa.");
            }

            return User
                    .withUsername(clinicaEncontrada.getEmail())
                    .password(clinicaEncontrada.getSenha())
                    .roles("CLINICA")
                    .build();
        }

        var admin = adminRepository.findByEmail(email);

        if (admin.isPresent()) {
            var adminEncontrado = admin.get();

            if (adminEncontrado.getAtivo() != 'S') {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Acesso administrativo desativado.");
            }

            return User
                    .withUsername(adminEncontrado.getEmail())
                    .password(adminEncontrado.getSenha())
                    .roles("ADMIN")
                    .build();
        }

        throw new UsernameNotFoundException(
                "Usuário não encontrado: " + email
        );
    }
}
