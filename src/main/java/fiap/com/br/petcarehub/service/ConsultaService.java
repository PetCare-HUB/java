package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.entity.Consulta;
import fiap.com.br.petcarehub.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository repository;

    private Consulta findConsultaById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "ID " + id + " não encontrado"
                )
        );
    }

    public List<Consulta> findAll() {
        return repository.findAll();
    }

    public Consulta add(Consulta consulta) {
        return repository.save(consulta);
    }

    public Consulta findById(Long id) {
        return findConsultaById(id);
    }

    public void delete(Long id) {
        findConsultaById(id);
        repository.deleteById(id);
    }

    public Consulta update(Long id, Consulta newConsulta) {
        findConsultaById(id);
        newConsulta.setId(id);
        return repository.save(newConsulta);
    }
}