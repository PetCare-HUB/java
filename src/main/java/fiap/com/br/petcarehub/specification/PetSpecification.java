package fiap.com.br.petcarehub.specification;

import fiap.com.br.petcarehub.entity.Pet;
import fiap.com.br.petcarehub.entity.ScoreSaude;
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
            Integer scoreMax,
            Long tutorId
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tutor só busca entre os próprios pets - não vem de parâmetro do
            // cliente, e sim do JWT autenticado (ver PetService.buscarComFiltros).
            if (tutorId != null) {
                predicates.add(cb.equal(root.get("tutor").get("id"), tutorId));
            }

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

            if (scoreMin != null || scoreMax != null) {

                var scoreSubquery = query.subquery(Long.class);
                var scoreRoot = scoreSubquery.from(ScoreSaude.class);

                List<Predicate> scorePredicates = new ArrayList<>();
                scorePredicates.add(
                        cb.equal(scoreRoot.get("pet").get("id"), root.get("id")));

                if (scoreMin != null) {
                    scorePredicates.add(cb.greaterThanOrEqualTo(scoreRoot.get("scoreTotal"), scoreMin));
                }

                // Score máximo
                if (scoreMax != null) {
                    scorePredicates.add(
                            cb.lessThanOrEqualTo(scoreRoot.get("scoreTotal"), scoreMax));
                }
                var scoreMaisRecenteSubquery = query.subquery(Long.class);
                var scoreMaisRecenteRoot =
                        scoreMaisRecenteSubquery.from(ScoreSaude.class);

                scoreMaisRecenteSubquery.select(
                        cb.literal(1L)
                );

                scoreMaisRecenteSubquery.where(cb.equal(scoreMaisRecenteRoot.get("pet").get("id"), root.get("id")),
                        cb.or(cb.greaterThan(scoreMaisRecenteRoot.get("dataCalculo"), scoreRoot.get("dataCalculo")),
                                cb.and(cb.equal(scoreMaisRecenteRoot.get("dataCalculo"),scoreRoot.get("dataCalculo")),
                                        cb.greaterThan(scoreMaisRecenteRoot.get("id"), scoreRoot.get("id"))))
                );

                scorePredicates.add(cb.not(cb.exists(scoreMaisRecenteSubquery)));

                scoreSubquery.select(cb.literal(1L));

                scoreSubquery.where(scorePredicates.toArray(new Predicate[0]));

                predicates.add(cb.exists(scoreSubquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}