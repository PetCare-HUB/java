package fiap.com.br.petcarehub.controller;

import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.dto.response.PageResponse;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @GetMapping("/clinicas")
    @Operation(summary = "Listar clínicas da rede",
            description = "Fila de credenciamento. Use status=PRE_CADASTRADO para ver só quem aguarda aprovação.")
    public PageResponse<ClinicaResponse> listarClinicas(
            @RequestParam(required = false) StatusAcesso status,
            @ParameterObject @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PageResponse<>(service.listarClinicas(status, pageable));
    }

    @PutMapping("/clinicas/{id}/ativar")
    @Operation(summary = "Aprovar clínica",
            description = "Libera o acesso da clínica à plataforma. A partir daqui ela consegue autenticar.")
    public ClinicaResponse ativarClinica(@PathVariable Long id) {
        return service.aprovar(id);
    }

    @PutMapping("/clinicas/{id}/bloquear")
    @Operation(summary = "Bloquear clínica",
            description = "Revoga o acesso da clínica sem apagar o cadastro nem o histórico dos pets.")
    public ClinicaResponse bloquearClinica(@PathVariable Long id) {
        return service.bloquear(id);
    }
}
