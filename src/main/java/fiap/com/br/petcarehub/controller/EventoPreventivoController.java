package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.request.EventoPreventivoRequest;
import fiap.com.br.petcarehub.dto.response.EventoPreventivoResponse;
import fiap.com.br.petcarehub.service.EventoPreventivoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eventos-preventivos")
@RequiredArgsConstructor
public class EventoPreventivoController {

    private final EventoPreventivoService service;

    @PostMapping
    @Operation(summary = "Criar evento preventivo")
    public ResponseEntity<EventoPreventivoResponse> criar(@RequestBody @Valid EventoPreventivoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}/realizar")
    @Operation(summary = "Marcar evento preventivo como realizado")
    public EventoPreventivoResponse marcarComoRealizado(@PathVariable Long id) {
        return service.marcarComoRealizado(id);
    }
}
