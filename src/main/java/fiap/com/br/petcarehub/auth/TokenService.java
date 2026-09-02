package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.response.LoginResponse;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import fiap.com.br.petcarehub.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;
    private final TutorRepository tutorRepository;
    private final ClinicaRepository clinicaRepository;

    public LoginResponse generateToken(Authentication authentication) {

        var now = Instant.now();
        var expiracao = now.plus(30, ChronoUnit.MINUTES);

        var role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .orElse(null);

        var claimsBuilder = JwtClaimsSet.builder()
                .issuer("petcare-hub-api")
                .issuedAt(now)
                .expiresAt(expiracao)
                .subject(authentication.getName())
                .claim("role", role);

        Long tutorId = null;
        Long clinicaId = null;
        var tutor = tutorRepository.findByEmail(authentication.getName());
        if (tutor.isPresent()) {
            tutorId = tutor.get().getId();
            claimsBuilder
                    .claim("tutorId", tutorId);
        } else {
            var clinica = clinicaRepository.findByEmail(authentication.getName());

            if (clinica.isPresent()) {
                clinicaId = clinica.get().getId();
                claimsBuilder
                        .claim("clinicaId", clinicaId);
            }
        }

        var claims = claimsBuilder.build();

        String token = encoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
        return new LoginResponse(
                token,
                "Bearer",
                expiracao.toString(),
                role,
                tutorId,
                clinicaId
        );
    }
}