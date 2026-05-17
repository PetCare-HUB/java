package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
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
public class ClinicaService {

    private final ClinicaRepository repository;

    private Clinica findClinicaById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ID " + id + " não encontrado"
                )
        );
    }

    public List<Clinica> findAll() {
        return repository.findAll();
    }

    public Page<Clinica> getAllPaginado(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Clinica add(Clinica clinica) {
        return repository.save(clinica);
    }

    public Clinica findById(Long id) {
        return findClinicaById(id);
    }

    public void delete(Long id) {
        findClinicaById(id);
        repository.deleteById(id);
    }

    public Clinica update(Long id, Clinica newClinica) {
        findClinicaById(id);
        newClinica.setId(id);
        return repository.save(newClinica);
    }
}