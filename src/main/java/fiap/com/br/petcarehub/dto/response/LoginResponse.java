package fiap.com.br.petcarehub.dto.response;

public record LoginResponse (
        String token,
        String tipo,
        String expiraEm,
        String role,
        Long tutorId,
        Long clinicaId){
}
