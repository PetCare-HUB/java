package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.request.AtivarContaRequest;
import fiap.com.br.petcarehub.dto.request.LoginRequest;
import fiap.com.br.petcarehub.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    public final AuthenticationManager authenticationManager;
    public final TokenService tokenService;
    private final AuthService authService;


    @PostMapping("/ativar-conta")
    public ResponseEntity<LoginResponse> ativarConta(
            @Valid @RequestBody AtivarContaRequest request
    ) {
        var response = authService.ativarConta(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        return tokenService.generateToken(auth);
    }
}
