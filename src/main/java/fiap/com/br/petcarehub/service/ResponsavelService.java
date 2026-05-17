package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.projection.ResponsavelSummary;
import fiap.com.br.petcarehub.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ResponsavelService {

    private final ResponsavelRepository repository;

    private Responsavel findResponsavelById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ID " + id + " não encontrado"
                )
        );
    }

    public List<Responsavel> findAll() {
        return repository.findAll();
    }

    public Page<Responsavel> getAllPaginado(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Responsavel add(Responsavel responsavel) {
        return repository.save(responsavel);
    }

    public Responsavel findById(Long id) {
        return findResponsavelById(id);
    }

    public void delete(Long id) {
        findResponsavelById(id);
        repository.deleteById(id);
    }

    public Responsavel update(Long id, Responsavel newResponsavel) {
        findResponsavelById(id);
        newResponsavel.setId(id);
        return repository.save(newResponsavel);
    }
    public Page<ResponsavelSummary> getByNome(String nome, Pageable pageable) {
        return repository.findByNomeContaining(nome, pageable);
    }

    public Page<ResponsavelSummary> getByEmail(String email, Pageable pageable) {
        return repository.findByEmailContaining(email, pageable);
    }

    public Page<ResponsavelSummary> getByCpf(String cpf, Pageable pageable) {
        return repository.findByCpf(cpf, pageable);
    }

    public Page<ResponsavelSummary> getByTelefone(String telefone, Pageable pageable) {
        return repository.findByTelefoneContaining(telefone, pageable
        );
    }
}