package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.response.TimelinePetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final PetService petService;
    private final ConsultaService consultaService;
    private final LeituraIotService leituraIotService;
    private final AlertaSaudeService alertaSaudeService;
    private final ScoreSaudeService scoreSaudeService;
    private final EventoPreventivoService eventoPreventivoService;

    @Transactional(readOnly = true)
    public TimelinePetResponse timeline(Long petId) {
        return new TimelinePetResponse(
                petService.buscarPorId(petId),
                consultaService.ultimasPorPet(petId),
                leituraIotService.ultimasColeira(petId),
                leituraIotService.ultimasComedouro(petId),
                leituraIotService.ultimasAmbiente(petId),
                alertaSaudeService.ultimosPorPet(petId),
                scoreSaudeService.historico(petId),
                eventoPreventivoService.planoDoPet(petId)
        );
    }
}
