package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.ResponsavelRequest;
import fiap.com.br.petcarehub.dto.response.ResponsavelResponse;
import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository repository;

    @Transactional(readOnly = true)
    public Page<ResponsavelResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Responsavel findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsável não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public ResponsavelResponse buscarPorId(Long id) {
        return DtoMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public ResponsavelResponse criar(ResponsavelRequest request) {
        Responsavel responsavel = Responsavel.builder()
                .nome(request.nome())
                .email(request.email())
                .telefone(request.telefone())
                .cpf(request.cpf())
                .build();
        return DtoMapper.toResponse(repository.save(responsavel));
    }

    @Transactional
    public ResponsavelResponse atualizar(Long id, ResponsavelRequest request) {
        Responsavel responsavel = findEntityById(id);
        responsavel.setNome(request.nome());
        responsavel.setEmail(request.email());
        responsavel.setTelefone(request.telefone());
        responsavel.setCpf(request.cpf());
        return DtoMapper.toResponse(repository.save(responsavel));
    }

    @Transactional
    public void deletar(Long id) {
        Responsavel responsavel = findEntityById(id);
        repository.delete(responsavel);
    }

    @Transactional(readOnly = true)
    public Page<ResponsavelResponse> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ResponsavelResponse> buscarPorEmail(String email, Pageable pageable) {
        return repository.findByEmailContainingIgnoreCase(email, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ResponsavelResponse> buscarPorCpf(String cpf, Pageable pageable) {
        return repository.findByCpfContaining(cpf, pageable).map(DtoMapper::toResponse);
    }
}
