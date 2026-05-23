package fiap.com.br.petcarehub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClinicaMetricasResponse(
		Long clinicaId,
		String clinicaNome,
		String periodo,
		LocalDateTime inicio,
		LocalDateTime fim,
		Long totalPets,
		Long petsAtivos,
		Long petsEmRisco,
		Long consultasNoPeriodo,
		Long eventosPreventivosNoPeriodo,
		Long eventosPreventivosPendentes,
		Long alertasAbertos,
		Long scoresNoPeriodo,
		BigDecimal scoreMedio,
		Long scoresVermelhos,
		Long scoresAmarelos,
		Long scoresVerdes
) {}

