package fiap.com.br.petcarehub.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Le os claims do JWT autenticado (role, tutorId, clinicaId) direto do
 * SecurityContext, sem depender do que o cliente manda no corpo/parametros
 * da requisicao - evita que um tutor se passe por outro so trocando um id.
 */
public final class CurrentUser {

    private CurrentUser() {}

    private static Jwt jwt() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String role() {
        return jwt().getClaim("role");
    }

    public static boolean isTutor() {
        return "TUTOR".equals(role());
    }

    public static boolean isClinica() {
        return "CLINICA".equals(role());
    }

    public static Long tutorId() {
        Object valor = jwt().getClaim("tutorId");
        return valor == null ? null : ((Number) valor).longValue();
    }

    public static Long clinicaId() {
        Object valor = jwt().getClaim("clinicaId");
        return valor == null ? null : ((Number) valor).longValue();
    }
}
