package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.ClinicaRequest;
import fiap.com.br.petcarehub.dto.response.ClinicaResponse;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository repository;

    @Transactional(readOnly = true)
    public Page<ClinicaResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Clinica findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clínica não encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public ClinicaResponse buscarPorId(Long id) {
        return DtoMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public ClinicaResponse criar(ClinicaRequest request) {
        Clinica clinica = Clinica.builder()
                .nome(request.nome())
                .cnpj(request.cnpj())
                .endereco(request.endereco())
                .telefone(request.telefone())
                .build();
        return DtoMapper.toResponse(repository.save(clinica));
    }

    @Transactional
    public ClinicaResponse atualizar(Long id, ClinicaRequest request) {
        Clinica clinica = findEntityById(id);
        clinica.setNome(request.nome());
        clinica.setCnpj(request.cnpj());
        clinica.setEndereco(request.endereco());
        clinica.setTelefone(request.telefone());
        return DtoMapper.toResponse(repository.save(clinica));
    }

    @Transactional
    public void deletar(Long id) {
        Clinica clinica = findEntityById(id);
        repository.delete(clinica);
    }

    @Transactional(readOnly = true)
    public Page<ClinicaResponse> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable).map(DtoMapper::toResponse);
    }
}
