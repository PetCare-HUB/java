package fiap.com.br.petcarehub.auth;

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

    public String generateToken(Authentication authentication) {

        var now = Instant.now();

        var role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority);

        var claims = JwtClaimsSet.builder()
                .issuer("petcare-hub-api")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .subject(authentication.getName())
                .claim("role", role)
                .build();

        return encoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
