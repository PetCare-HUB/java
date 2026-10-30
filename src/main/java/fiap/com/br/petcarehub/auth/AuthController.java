package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.request.AtivarContaRequest;
import fiap.com.br.petcarehub.dto.request.CadastroClinicaRequest;
import fiap.com.br.petcarehub.dto.request.LoginRequest;
import fiap.com.br.petcarehub.dto.response.LoginResponse;
import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
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

    @PostMapping("/cadastro-clinica")
    public ResponseEntity<ClinicaResponse> cadastrarClinica(
            @Valid @RequestBody CadastroClinicaRequest request
    ) {
        return ResponseEntity.status(201).body(authService.cadastrarClinica(request));
    }

    // Sem /ativar-clinica: ele existia para as clínicas pré-cadastradas sem
    // senha definirem uma, provando posse de um código de credenciamento que
    // era o MESMO para toda a rede. Quem tivesse esse código assumia qualquer
    // clínica sem senha — e o CNPJ, que é metade da prova, é público.
    //
    // Nenhuma clínica nasce mais sem senha (nem pelo cadastro público, nem
    // pelo POST /clinicas do admin, nem pelo .NET, que não cadastra mais),
    // então o endpoint ficou sem uso legítimo e só restava o risco.

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
