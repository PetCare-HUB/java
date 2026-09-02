package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.request.AtivarContaRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
        var jwt = authService.ativarConta(request);

        return ResponseEntity.ok(
                new LoginResponse(jwt)
        );
    }



    public record LoginRequest(@NotBlank String Username, @NotBlank String password) {}
    public record LoginResponse(String token) { }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.Username(),
                        request.password()
                )
        );

        var jwt = tokenService.generateToken(auth);

        return new LoginResponse(jwt);
    }
}
