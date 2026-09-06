package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    Page<Pet> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    long countByClinicaId(Long clinicaId);

    long countByClinicaIdAndAtivoTrue(Long clinicaId);


}