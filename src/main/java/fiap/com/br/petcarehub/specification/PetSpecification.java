package fiap.com.br.petcarehub.specification;

import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.enums.EspeciePet;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PetSpecification {

    private PetSpecification() {}

    public static Specification<Pet> filtrar(
            EspeciePet especie,
            String raca,
            Long clinicaId,
            Integer scoreMin,
            Integer scoreMax
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (especie != null) {
                predicates.add(cb.equal(root.get("especie"), especie));
            }

            if (raca != null && !raca.isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("raca")),
                        "%" + raca.toLowerCase() + "%",
                        '\\'
                ));
            }

            if (clinicaId != null) {
                predicates.add(cb.equal(root.get("clinica").get("id"), clinicaId));
            }

            if (scoreMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("scoreAtual"), scoreMin));
            }

            if (scoreMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("scoreAtual"), scoreMax));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}