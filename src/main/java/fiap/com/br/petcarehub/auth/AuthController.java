package fiap.com.br.petcarehub.auth;

import fiap.com.br.petcarehub.dto.request.AtivarContaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/ativar-conta")
    public ResponseEntity<Void> ativarConta(
            @Valid @RequestBody AtivarContaRequest request
    ) {
        authService.ativarConta(request);
        return ResponseEntity.noContent().build();
    }
}
