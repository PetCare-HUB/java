package fiap.com.br.petcarehub.keepalive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class KeepAliveController {

    @GetMapping("/keepalive")
    public ResponseEntity<Map<String, String>> keepAlive() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "Olá! Você está na página de Keep Alive do PetCare Hub."
        ));
    }
}

