package fiap.com.br.petcarehub.dto.response;

import java.util.List;

public record TimelinePetResponse(
        PetResponse pet,
        List<ConsultaResponse> consultas,
        List<LeituraColeiraResponse> leiturasColeira,
        List<LeituraComedouroResponse> leiturasComedouro,
        List<LeituraAmbienteResponse> leiturasAmbiente,
        List<AlertaSaudeResponse> alertas,
        List<ScoreSaudeResponse> scores,
        List<EventoPreventivoResponse> eventosPreventivos
) {}
