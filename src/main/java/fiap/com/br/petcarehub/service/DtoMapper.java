package fiap.com.br.petcarehub.service;

import fiap.com.br.petcarehub.dto.response.*;
import fiap.com.br.petcarehub.entity.*;
import fiap.com.br.petcarehub.dto.request.ClinicaRequest;
import fiap.com.br.petcarehub.dto.request.*;;

public final class DtoMapper {

    private DtoMapper() {}

    public static ResponsavelResponse toResponse(Responsavel responsavel) {
        return new ResponsavelResponse(
                responsavel.getId(),
                responsavel.getNome(),
                responsavel.getEmail(),
                responsavel.getTelefone(),
                responsavel.getCpf(),
                responsavel.getDataCadastro()
        );
    }

    public static ResponsavelResumoResponse toResumo(Responsavel responsavel) {
        return new ResponsavelResumoResponse(
                responsavel.getId(),
                responsavel.getNome(),
                responsavel.getEmail()
        );
    }

    public static ClinicaResponse toResponse(Clinica clinica) {
        return new ClinicaResponse(
                clinica.getId(),
                clinica.getNome(),
                clinica.getCnpj(),
                clinica.getEndereco(),
                clinica.getTelefone()
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
                pet.getScoreAtual(),
                pet.getDataCadastro(),
                toResumo(pet.getResponsavel()),
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
                consulta.getObservacoes(),
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
                alerta.getResolvido(),
                alerta.getDataCriacao(),
                alerta.getDataResolucao()
        );
    }

    public static ScoreSaudeResponse toResponse(ScoreSaude score) {
        return new ScoreSaudeResponse(
                score.getId(),
                score.getPet().getId(),
                score.getPet().getNome(),
                score.getScore(),
                score.getCategoria(),
                score.getObservacao(),
                score.getDataCalculo()
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
                evento.getRealizado()
        );
    }

    public static ProtocoloPreventivoResponse toResponse(ProtocoloPreventivo protocolo) {
        return new ProtocoloPreventivoResponse(
                protocolo.getId(),
                protocolo.getEspecie(),
                protocolo.getRaca(),
                protocolo.getTipo(),
                protocolo.getNome(),
                protocolo.getDescricao(),
                protocolo.getIdadeMesesAplicacao(),
                protocolo.getIntervaloReforcoDias()
        );
    }

    public static Clinica toClinica(ClinicaRequest request) {
        return Clinica.builder()
                .nome(request.nome())
                .cnpj(request.cnpj())
                .endereco(request.endereco())
                .telefone(request.telefone())
                .build();
    }

    public static void updateClinica(Clinica clinica, ClinicaRequest request) {
        if (request.nome() != null) clinica.setNome(request.nome());
        if (request.cnpj() != null) clinica.setCnpj(request.cnpj());
        if (request.endereco() != null) clinica.setEndereco(request.endereco());
        if (request.telefone() != null) clinica.setTelefone(request.telefone());
    }
}
