package fiap.com.br.petcarehub.specification;

import fiap.com.br.petcarehub.entity.AlertaSaude;
import fiap.com.br.petcarehub.enums.TipoAlerta;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AlertaSaudeSpecification {

    private AlertaSaudeSpecification() {}

    public static Specification<AlertaSaude> filtrar(Long petId, TipoAlerta tipo, Boolean resolvido, Long tutorId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (petId != null) {
                predicates.add(cb.equal(root.get("pet").get("id"), petId));
            }

            // Tutor só vê alertas dos próprios pets - não vem de parâmetro do
            // cliente, e sim do JWT autenticado (ver AlertaSaudeService.buscar).
            if (tutorId != null) {
                predicates.add(cb.equal(root.get("pet").get("tutor").get("id"), tutorId));
            }

            if (tipo != null) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }

            if (resolvido != null) {
                predicates.add(cb.equal(root.get("resolvido"), resolvido));
            }

            if (predicates.isEmpty()) {return cb.conjunction();}

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}