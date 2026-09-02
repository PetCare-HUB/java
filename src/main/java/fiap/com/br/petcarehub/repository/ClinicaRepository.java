package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Clinica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClinicaRepository extends JpaRepository<Clinica, Long> {
    Page<Clinica> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Optional<Clinica> findByEmail(String email);
    boolean existsByCnpj(String cnpj);
}
