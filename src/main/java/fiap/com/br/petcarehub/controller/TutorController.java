package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.PageResponse;
import fiap.com.br.petcarehub.dto.request.TutorRequest;
import fiap.com.br.petcarehub.dto.response.TutorResponse;
import fiap.com.br.petcarehub.service.TutorService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutor")
@RequiredArgsConstructor
public class TutorController {

    private final TutorService service;

    @GetMapping
    @Operation(summary = "Listar responsáveis", description = "Lista responsáveis com paginação e ordenação.")
    public PageResponse<TutorResponse> listar(@PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PageResponse<>(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar responsável por ID")
    public TutorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar o próprio perfil", description = "Retorna os dados do tutor autenticado, incluindo a clínica do pré-cadastro (útil antes de ter qualquer pet cadastrado).")
    public TutorResponse me() {
        return service.buscarPerfilAutenticado();
    }

    @PostMapping
    @Operation(summary = "Criar responsável")
    public ResponseEntity<TutorResponse> criar(@RequestBody @Valid TutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar responsável")
    public TutorResponse atualizar(@PathVariable Long id, @RequestBody @Valid TutorRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir responsável")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/nome")
    @Operation(summary = "Buscar responsáveis por nome")
    public PageResponse<TutorResponse> buscarPorNome(@RequestParam String nome, Pageable pageable) {
        return new PageResponse<>(service.buscarPorNome(nome, pageable));
    }

    @GetMapping("/email")
    @Operation(summary = "Buscar responsáveis por email")
    public PageResponse<TutorResponse> buscarPorEmail(@RequestParam String email, Pageable pageable) {
        return new PageResponse<>(service.buscarPorEmail(email, pageable));
    }

    @GetMapping("/cpf")
    @Operation(summary = "Buscar responsáveis por CPF")
    public PageResponse<TutorResponse> buscarPorCpf(@RequestParam String cpf, Pageable pageable) {
        return new PageResponse<>(service.buscarPorCpf(cpf, pageable));
    }
}
