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
 * regra de negócio própria. A {@code PKG_CARGA.PRC_INS_LEITURA_COLEIRA}, por
 * exemplo, valida o pet, insere usando a sequence e dispara o alerta de
 * atividade baixa via {@code PKG_CARGA.PRC_INS_ALERTA_SAUDE} — a regra
 * "dormindo demais = risco" implementada no banco.</p>
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

    /**
     * Os tres objetos vivem dentro de packages desde a Sprint 4, e o nome do
     * package entra como <i>catalog</i>: e nessa posicao dos metadados JDBC
     * que o driver Oracle devolve o package. Sem isso o SimpleJdbcCall
     * procuraria o objeto solto de mesmo nome, que ainda existe no schema em
     * paralelo -- a chamada funcionaria e nao passaria pelo package.
     */
    public PlSqlRepository(DataSource dataSource) {
        this.inserirLeituraColeira = new SimpleJdbcCall(dataSource)
                .withCatalogName("PKG_CARGA")
                .withProcedureName("PRC_INS_LEITURA_COLEIRA");

        this.scoreMedioPet = new SimpleJdbcCall(dataSource)
                .withCatalogName("PKG_UTIL")
                .withFunctionName("FN_SCORE_MEDIO_PET");

        this.idadePet = new SimpleJdbcCall(dataSource)
                .withCatalogName("PKG_UTIL")
                .withFunctionName("FN_CALCULAR_IDADE_PET");
    }

    /**
     * Grava a leitura da coleira pela procedure do banco, que também cria o
     * alerta de atividade baixa quando o pet aparece como SEDENTARIO.
     */
    public void inserirLeituraColeira(Long petId, StatusAtividade statusAtividade,
                                      Integer nivelBateria, LocalDateTime timestampLeitura) {
        log.debug("Chamando PKG_CARGA.PRC_INS_LEITURA_COLEIRA para o pet {}", petId);
        inserirLeituraColeira.execute(Map.of(
                "P_ID_PET", petId,
                "P_STATUS_ATIVIDADE", statusAtividade.name(),
                "P_NIVEL_BATERIA", nivelBateria,
                "P_TIMESTAMP_LEITURA", Timestamp.valueOf(timestampLeitura)
        ));
    }

    /** Média dos scores do pet no período, por PKG_UTIL.FN_SCORE_MEDIO_PET. */
    public Double scoreMedio(Long petId, int dias) {
        return paraDouble(scoreMedioPet.executeFunction(Number.class, Map.of(
                "P_ID_PET", petId,
                "P_DIAS", dias
        )));
    }

    /** Idade do pet em meses, por PKG_UTIL.FN_CALCULAR_IDADE_PET. */
    public Double idadeEmMeses(Long petId) {
        return paraDouble(idadePet.executeFunction(Number.class, Map.of("P_ID_PET", petId)));
    }

    /**
     * NUMBER do Oracle chega como BigDecimal, não como Double: pedir Double
     * direto ao executeFunction estoura ClassCastException. Converter pelo
     * Number cobre qualquer tipo numérico que o driver resolva devolver.
     */
    private static Double paraDouble(Number valor) {
        return valor == null ? null : valor.doubleValue();
    }
}
