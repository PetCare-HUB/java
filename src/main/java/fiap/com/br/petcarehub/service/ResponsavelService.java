package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.entity.Responsavel;
import fiap.com.br.petcarehub.repository.ResponsavelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResponsavelService {

    @Autowired
    private ResponsavelRepository repository;

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

    public Page<Responsavel> getAllProdutosPaginado(Pageable pageable) {
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
}