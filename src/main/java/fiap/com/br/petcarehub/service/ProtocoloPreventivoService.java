package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.ProtocoloPreventivoRequest;
import fiap.com.br.petcarehub.dto.response.ProtocoloPreventivoResponse;
import fiap.com.br.petcarehub.entity.ProtocoloPreventivo;
import fiap.com.br.petcarehub.enums.EspeciePet;
import fiap.com.br.petcarehub.repository.ProtocoloPreventivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProtocoloPreventivoService {

    private final ProtocoloPreventivoRepository repository;

    @Transactional(readOnly = true)
    public Page<ProtocoloPreventivoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DtoMapper::toResponse);
    }

    @Cacheable(value = "protocolos", key = "#especie")
    @Transactional(readOnly = true)
    public List<ProtocoloPreventivoResponse> buscarPorEspecie(EspeciePet especie) {
        return repository.findByEspecieOrderByIdadeMesesAplicacaoAsc(especie).stream().map(DtoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProtocoloPreventivo findEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Protocolo preventivo não encontrado: " + id));
    }

    @CacheEvict(value = "protocolos", allEntries = true)
    @Transactional
    public ProtocoloPreventivoResponse criar(ProtocoloPreventivoRequest request) {
        ProtocoloPreventivo protocolo = ProtocoloPreventivo.builder()
                .especie(request.especie())
                .raca(request.raca())
                .tipo(request.tipo())
                .nome(request.nome())
                .descricao(request.descricao())
                .idadeMesesAplicacao(request.idadeMesesAplicacao())
                .intervaloReforcoDias(request.intervaloReforcoDias())
                .build();
        return DtoMapper.toResponse(repository.save(protocolo));
    }
}
