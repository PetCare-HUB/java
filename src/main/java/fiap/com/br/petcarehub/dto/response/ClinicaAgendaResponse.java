package fiap.com.br.petcarehub.dto.response;

import java.time.LocalDate;
import java.util.List;

public record ClinicaAgendaResponse(
		Long clinicaId,
		String clinicaNome,
		LocalDate inicio,
		LocalDate fim,
		List<ConsultaResponse> consultas,
		List<EventoPreventivoResponse> eventosPreventivos
) {}

