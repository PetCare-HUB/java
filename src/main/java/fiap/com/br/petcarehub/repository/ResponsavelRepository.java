package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Responsavel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    Page<Responsavel> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Responsavel> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Responsavel> findByCpfContaining(String cpf, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
