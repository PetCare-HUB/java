package fiap.com.br.petcarehub.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LeituraComedouroResponse(
        Long id,
        Long petId,
        String petNome,
        Integer nivelRacaoPct,
        BigDecimal pesoConsumidoG,
        LocalDateTime timestampLeitura
) {}
