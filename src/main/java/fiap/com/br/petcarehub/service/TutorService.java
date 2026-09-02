package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.TutorRequest;
import fiap.com.br.petcarehub.dto.response.TutorResponse;
import fiap.com.br.petcarehub.entity.Tutor;
import fiap.com.br.petcarehub.enums.Role;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TutorService {
    private final PasswordEncoder passwordEncoder;
    private final TutorRepository repository;

    @Transactional(readOnly = true)
    public Page<TutorResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Tutor findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Responsável não encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public TutorResponse buscarPorId(Long id) {
        return DtoMapper.toResponse(findEntityById(id));
    }

    @Transactional
    public TutorResponse criar(TutorRequest request) {
        Role role = request.role() != null
                ? request.role()
                : Role.TUTOR;

        Tutor.TutorBuilder builder = Tutor.builder()
                .nome(request.nome())
                .email(request.email())
                .telefone(request.telefone())
                .cpf(request.cpf())
                .role(role)
                .statusAcesso(StatusAcesso.PRE_CADASTRADO);

        if (request.senha() != null && !request.senha().isBlank()) {
            builder.senha(passwordEncoder.encode(request.senha()));
        }

        Tutor tutor = builder.build();
        return DtoMapper.toResponse(repository.save(tutor));
    }

    @Transactional
    public TutorResponse atualizar(Long id, TutorRequest request) {
        Tutor tutor = findEntityById(id);
        tutor.setNome(request.nome());
        tutor.setEmail(request.email());
        tutor.setTelefone(request.telefone());
        tutor.setCpf(request.cpf());
        return DtoMapper.toResponse(repository.save(tutor));
    }

    @Transactional
    public void deletar(Long id) {
        Tutor tutor = findEntityById(id);
        repository.delete(tutor);
    }

    @Transactional(readOnly = true)
    public Page<TutorResponse> buscarPorNome(String nome, Pageable pageable) {
        return repository.findByNomeContainingIgnoreCase(nome, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TutorResponse> buscarPorEmail(String email, Pageable pageable) {
        return repository.findByEmailContainingIgnoreCase(email, pageable).map(DtoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<TutorResponse> buscarPorCpf(String cpf, Pageable pageable) {
        return repository.findByCpfContaining(cpf, pageable).map(DtoMapper::toResponse);
    }
}
