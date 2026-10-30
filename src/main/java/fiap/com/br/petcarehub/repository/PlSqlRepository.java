package fiap.com.br.petcarehub.repository;

import fiap.com.br.petcarehub.enums.StatusAtividade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Chamadas ao código PL/SQL que vive no Oracle (disciplina de Database).
 *
 * <p>Estas rotinas não são um espelho do que o Java já faz: elas carregam
 * regra de negócio própria. A {@code PRC_INS_LEITURA_COLEIRA}, por exemplo,
 * valida o pet, insere usando a sequence e dispara o alerta de atividade
 * baixa via {@code PRC_INS_ALERTA_SAUDE} — a regra "dormindo demais = risco"
 * implementada no banco.</p>
 *
 * <p><b>Cuidado ao usar:</b> as procedures de carga terminam com
 * {@code COMMIT} e tratam exceção com {@code WHEN OTHERS} gravando em
 * LOG_ERROS, sem relançar. Ou seja, elas <i>retornam normalmente mesmo
 * quando falham</i>. Quem chama precisa conferir o efeito, e é por isso que
 * o serviço relê a leitura depois de inserir em vez de confiar no retorno.</p>
 */
@Slf4j
@Repository
public class PlSqlRepository {

    private final SimpleJdbcCall inserirLeituraColeira;
    private final SimpleJdbcCall scoreMedioPet;
    private final SimpleJdbcCall idadePet;

    public PlSqlRepository(DataSource dataSource) {
        this.inserirLeituraColeira = new SimpleJdbcCall(dataSource)
                .withProcedureName("PRC_INS_LEITURA_COLEIRA");

        this.scoreMedioPet = new SimpleJdbcCall(dataSource)
                .withFunctionName("FN_SCORE_MEDIO_PET");

        this.idadePet = new SimpleJdbcCall(dataSource)
                .withFunctionName("FN_CALCULAR_IDADE_PET");
    }

    /**
     * Grava a leitura da coleira pela procedure do banco, que também cria o
     * alerta de atividade baixa quando o pet aparece como SEDENTARIO.
     */
    public void inserirLeituraColeira(Long petId, StatusAtividade statusAtividade,
                                      Integer nivelBateria, LocalDateTime timestampLeitura) {
        log.debug("Chamando PRC_INS_LEITURA_COLEIRA para o pet {}", petId);
        inserirLeituraColeira.execute(Map.of(
                "P_ID_PET", petId,
                "P_STATUS_ATIVIDADE", statusAtividade.name(),
                "P_NIVEL_BATERIA", nivelBateria,
                "P_TIMESTAMP_LEITURA", Timestamp.valueOf(timestampLeitura)
        ));
    }

    /** Média dos scores do pet no período, calculada por FN_SCORE_MEDIO_PET. */
    public Double scoreMedio(Long petId, int dias) {
        return scoreMedioPet.executeFunction(Double.class, Map.of(
                "P_ID_PET", petId,
                "P_DIAS", dias
        ));
    }

    /** Idade do pet em meses, calculada por FN_CALCULAR_IDADE_PET. */
    public Double idadeEmMeses(Long petId) {
        return idadePet.executeFunction(Double.class, Map.of("P_ID_PET", petId));
    }
}
