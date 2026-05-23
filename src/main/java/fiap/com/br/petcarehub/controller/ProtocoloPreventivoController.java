package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.PageResponse;
import fiap.com.br.petcarehub.dto.request.ProtocoloPreventivoRequest;
import fiap.com.br.petcarehub.dto.response.ProtocoloPreventivoResponse;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.service.ProtocoloPreventivoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protocolos-preventivos")
@RequiredArgsConstructor
public class ProtocoloPreventivoController {

    private final ProtocoloPreventivoService service;

    @GetMapping
    @Operation(summary = "Listar protocolos preventivos")
    public PageResponse<ProtocoloPreventivoResponse> listar(@PageableDefault(size = 10, sort = "idadeMesesAplicacao", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PageResponse<>(service.listar(pageable));
    }

    @GetMapping("/por-especie")
    @Operation(summary = "Buscar protocolos por espécie", description = "Endpoint com cache, pois protocolos preventivos mudam pouco.")
    public List<ProtocoloPreventivoResponse> buscarPorEspecie(@RequestParam EspeciePet especie) {
        return service.buscarPorEspecie(especie);
    }

    @PostMapping
    @Operation(summary = "Criar protocolo preventivo")
    public ResponseEntity<ProtocoloPreventivoResponse> criar(@RequestBody @Valid ProtocoloPreventivoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }
}
