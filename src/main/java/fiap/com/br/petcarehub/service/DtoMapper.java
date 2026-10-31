package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.request.ClinicaRequest;
import fiap.com.br.petcarehub.dto.response.*;
import fiap.com.br.petcarehub.entity.*;
import fiap.com.br.petcarehub.enums.StatusAcesso;

public final class DtoMapper {

    /**
     * Devolvida no lugar da observação dos scores calculados antes da V12. É pública
     * para a IA reconhecer o registro histórico e não tratar a frase como explicação.
     */
    public static final String SEM_OBSERVACAO_HISTORICA =
            "Sem observação registrada: score calculado antes da versão que passou a explicar as variações.";

    private DtoMapper() {}

    public static TutorResponse toResponse(Tutor tutor) {
        return new TutorResponse(
                tutor.getId(),
                tutor.getNome(),
                tutor.getStatusAcesso(),
                tutor.getEmail(),
                tutor.getTelefone(),
                tutor.getCpf(),
                tutor.getDataCadastro(),
                tutor.getClinica() != null ? toResumo(tutor.getClinica()) : null
        );
    }

    public static TutorResumoResponse toResumo(Tutor tutor) {
        return new TutorResumoResponse(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail()
        );
    }

    public static ClinicaResponse toResponse(Clinica clinica) {
        return new ClinicaResponse(
                clinica.getId(),
                clinica.getNome(),
                clinica.getEmail(),
                clinica.getCnpj(),
                clinica.getEndereco(),
                clinica.getTelefone(),
                clinica.getAtivo(),
                clinica.getStatusAcesso()
        );
    }

    public static ClinicaResumoResponse toResumo(Clinica clinica) {
        return new ClinicaResumoResponse(
                clinica.getId(),
                clinica.getNome()
        );
    }

    public static PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getDataNascimento(),
                pet.getPesoKg(),
                pet.getSexo(),
                pet.getCondicoesCronicas(),
                pet.getAtivo(),
                pet.getDataCadastro(),
                toResumo(pet.getTutor()),
                toResumo(pet.getClinica())
        );
    }

    public static ConsultaResponse toResponse(Consulta consulta) {
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getPet().getId(),
                consulta.getPet().getNome(),
                consulta.getClinica().getId(),
                consulta.getClinica().getNome(),
                consulta.getDataConsulta(),
                consulta.getTipo(),
                consulta.getDescricao(),
                consulta.getDiagnostico(),
                consulta.getRetornoRecomendado(),
                consulta.getDataRetorno(),
                consulta.getValor()
        );
    }

    public static AlertaSaudeResponse toResponse(AlertaSaude alerta) {
        return new AlertaSaudeResponse(
                alerta.getId(),
                alerta.getPet().getId(),
                alerta.getPet().getNome(),
                alerta.getTipo(),
                alerta.getNivel(),
                alerta.getMensagem(),
                alerta.getValorDetectado(),
                alerta.getLimiteReferencia(),
                alerta.getResolvido(),
                alerta.getDataAlerta(),
                alerta.getDataResolucao()
        );
    }

    /** Separador usado por AnaliseSaudeService ao gravar as fontes em uma única coluna. */
    public static final String SEPARADOR_FONTES = ";";

    public static AnaliseIaResponse toResponse(AnaliseIa analise) {
        java.util.List<String> fontes = analise.getFontes() == null || analise.getFontes().isBlank()
                ? java.util.List.of()
                : java.util.Arrays.asList(analise.getFontes().split(SEPARADOR_FONTES));
        return new AnaliseIaResponse(
                analise.getId(),
                analise.getPet().getId(),
                analise.getPet().getNome(),
                analise.getTipo(),
                analise.getPergunta(),
                analise.getResposta(),
                analise.getScore() != null ? analise.getScore().getId() : null,
                analise.getAlerta() != null ? analise.getAlerta().getId() : null,
                analise.getModelo(),
                fontes,
                analise.getDataAnalise()
        );
    }

    public static ScoreSaudeResponse toResponse(ScoreSaude score) {
        return new ScoreSaudeResponse(
                score.getId(),
                score.getPet().getId(),
                score.getPet().getNome(),
                score.getScoreTotal(),
                score.getScoreAtividade(),
                score.getScoreAlimentacao(),
                score.getScoreAmbiente(),
                score.getScoreConsulta(),
                score.getScorePreventivo(),
                score.getCategoria(),
                score.getDataCalculo(),
                score.getObservacao() == null || score.getObservacao().isBlank()
                        ? SEM_OBSERVACAO_HISTORICA
                        : score.getObservacao()
        );
    }

    public static LeituraColeiraResponse toResponse(LeituraColeira leitura) {
        return new LeituraColeiraResponse(
                leitura.getId(),
                leitura.getPet().getId(),
                leitura.getPet().getNome(),
                leitura.getStatusAtividade(),
                leitura.getNivelBateria(),
                leitura.getTimestampLeitura()
        );
    }

    public static LeituraComedouroResponse toResponse(LeituraComedouro leitura) {
        return new LeituraComedouroResponse(
                leitura.getId(),
                leitura.getPet().getId(),
                leitura.getPet().getNome(),
                leitura.getNivelRacaoPct(),
                leitura.getPesoConsumidoG(),
                leitura.getTimestampLeitura()
        );
    }

    public static LeituraAmbienteResponse toResponse(LeituraAmbiente leitura) {
        return new LeituraAmbienteResponse(
                leitura.getId(),
                leitura.getPet().getId(),
                leitura.getPet().getNome(),
                leitura.getTemperaturaAmbiente(),
                leitura.getUmidadePct(),
                leitura.getQualidadeArPpm(),
                leitura.getPetPresente(),
                leitura.getTimestampLeitura()
        );
    }

    public static EventoPreventivoResponse toResponse(EventoPreventivo evento) {
        return new EventoPreventivoResponse(
                evento.getId(),
                evento.getPet().getId(),
                evento.getPet().getNome(),
                evento.getTipo(),
                evento.getDescricao(),
                evento.getDataPrevista(),
                evento.getStatus(),
                evento.getDataRealizacao()
        );
    }

    public static ProtocoloPreventivoResponse toResponse(ProtocoloPreventivo protocolo) {
        return new ProtocoloPreventivoResponse(
                protocolo.getId(),
                protocolo.getEspecie(),
                protocolo.getRaca(),
                protocolo.getTipo(),
                protocolo.getDescricao(),
                protocolo.getIdadeMesesRecomendada(),
                protocolo.getIntervaloDias(),
                protocolo.getAtivo()
        );
    }

    public static Clinica toClinica(ClinicaRequest request) {
        return Clinica.builder()
                .nome(request.nome())
                .email(request.email())
                // A senha e sobrescrita com o hash pelo ClinicaService.criar.
                .senha(request.senha())
                .cnpj(normalizarCnpj(request.cnpj()))
                .endereco(request.endereco())
                .telefone(request.telefone())
                // 'ativo' e NOT NULL com CHECK IN ('S','N'): sem isso o char
                // fica em '\u0000' e o INSERT viola ck_clinica_ativo.
                .ativo('S')
                // Este caminho e o POST /clinicas, restrito ao admin. Quem
                // cria ja e quem aprova, entao nasce ATIVO - exigir aprovacao
                // depois seriam dois portoes para a mesma decisao. O cadastro
                // publico (/auth/cadastro-clinica) e que nasce PRE_CADASTRADO.
                .statusAcesso(StatusAcesso.ATIVO)
                .build();
    }

    public static void updateClinica(Clinica clinica, ClinicaRequest request) {
        if (request.nome() != null) {clinica.setNome(request.nome());}
        if (request.email() != null) {clinica.setEmail(request.email());}
        // Senha NAO entra aqui: o update generico gravava o valor em texto
        // puro (sem passwordEncoder), o que corrompia o hash e derrubava o
        // login da clinica. Troca de senha precisa de fluxo proprio.
        if (request.cnpj() != null) {clinica.setCnpj(normalizarCnpj(request.cnpj()));}
        if (request.endereco() != null) {clinica.setEndereco(request.endereco());}
        if (request.telefone() != null) {clinica.setTelefone(request.telefone());}
    }

    /** A coluna CNPJ e VARCHAR2(14): formatado estoura ORA-12899. */
    private static String normalizarCnpj(String cnpj) {
        return cnpj == null ? null : cnpj.replaceAll("\\D", "");
    }
}
