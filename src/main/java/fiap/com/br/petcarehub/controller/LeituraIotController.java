package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.request.*;
import fiap.com.br.petcarehub.dto.response.*;
import fiap.com.br.petcarehub.service.LeituraIotService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leituras")
@RequiredArgsConstructor
public class LeituraIotController {

    private final LeituraIotService service;

    @PostMapping("/coleira")
    @Operation(summary = "Registrar leitura da coleira", description = "Simula a entrada MQTT da coleira smart e pode gerar alerta automático.")
    public ResponseEntity<LeituraColeiraResponse> registrarColeira(@RequestBody @Valid LeituraColeiraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarColeira(request));
    }

    @PostMapping("/comedouro")
    @Operation(summary = "Registrar leitura do comedouro", description = "Simula a entrada MQTT do comedouro inteligente e pode gerar alerta automático.")
    public ResponseEntity<LeituraComedouroResponse> registrarComedouro(@RequestBody @Valid LeituraComedouroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarComedouro(request));
    }

    @PostMapping("/ambiente")
    @Operation(summary = "Registrar leitura ambiente", description = "Simula a entrada MQTT do módulo de ambiente e pode gerar alerta automático.")
    public ResponseEntity<LeituraAmbienteResponse> registrarAmbiente(@RequestBody @Valid LeituraAmbienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarAmbiente(request));
    }
}
