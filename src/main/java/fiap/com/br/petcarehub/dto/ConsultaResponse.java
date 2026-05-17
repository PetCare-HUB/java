package fiap.com.br.petcarehub.dto;

import fiap.com.br.petcarehub.enums.TipoConsulta;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
public class ConsultaResponse {

    private Long id;

    private String nomePet;

    private String nomeClinica;

    private LocalDate dataConsulta;

    private TipoConsulta tipo;

    private String descricao;

    private String diagnostico;

    private BigDecimal valor;

    private Character retornoRecomendado;

    private LocalDate dataRetorno;

    public Long getId() {
        return id;
    }

    public String getNomePet() {
        return nomePet;
    }

    public String getNomeClinica() {
        return nomeClinica;
    }

    public LocalDate getDataConsulta() {
        return dataConsulta;
    }

    public TipoConsulta getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Character getRetornoRecomendado() {
        return retornoRecomendado;
    }

    public LocalDate getDataRetorno() {
        return dataRetorno;
    }
}