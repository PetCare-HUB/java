package fiap.com.br.petcarehub.auth;

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

    public String generateToken(Authentication authentication) {

        var now = Instant.now();

        var role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .orElse(null);

        var claimsBuilder = JwtClaimsSet.builder()
                .issuer("petcare-hub-api")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("role", role);

        var tutor = tutorRepository.findByEmail(authentication.getName());

        if (tutor.isPresent()) {

            claimsBuilder
                    .claim("tutorId", tutor.get().getId());

        } else {

            var clinica = clinicaRepository.findByEmail(authentication.getName());

            if (clinica.isPresent()) {

                claimsBuilder
                        .claim("clinicaId", clinica.get().getId());
            }
        }

        var claims = claimsBuilder.build();

        return encoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
