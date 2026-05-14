package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
