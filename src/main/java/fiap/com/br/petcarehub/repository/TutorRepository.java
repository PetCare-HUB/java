package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Tutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    Page<Tutor> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Tutor> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Page<Tutor> findByCpfContaining(String cpf, Pageable pageable);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
