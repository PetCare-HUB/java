package fiap.com.br.petcarehub.config;

import fiap.com.br.petcarehub.entity.*;
import fiap.com.br.petcarehub.enums.*;
import fiap.com.br.petcarehub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedDatabase(
            ResponsavelRepository responsavelRepository,
            ClinicaRepository clinicaRepository,
            PetRepository petRepository,
            ConsultaRepository consultaRepository,
            ProtocoloPreventivoRepository protocoloRepository,
            EventoPreventivoRepository eventoRepository,
            LeituraColeiraRepository leituraColeiraRepository,
            LeituraComedouroRepository leituraComedouroRepository,
            LeituraAmbienteRepository leituraAmbienteRepository,
            AlertaSaudeRepository alertaRepository,
            ScoreSaudeRepository scoreRepository
    ) {
        return args -> {
            if (responsavelRepository.count() > 0) {
                return;
            }

            Responsavel kelson = responsavelRepository.save(Responsavel.builder()
                    .nome("Kelson Silva")
                    .email("kelson.petcare@example.com")
                    .telefone("11999990000")
                    .cpf("12345678901")
                    .build());

            Responsavel ana = responsavelRepository.save(Responsavel.builder()
                    .nome("Ana Pereira")
                    .email("ana.petcare@example.com")
                    .telefone("11988887777")
                    .cpf("98765432100")
                    .build());

            Clinica clyvo = clinicaRepository.save(Clinica.builder()
                    .nome("Clyvo Vet Unidade Paulista")
                    .cnpj("12.345.678/0001-90")
                    .endereco("Av. Paulista, 1000 - São Paulo")
                    .telefone("1133334444")
                    .build());

            Pet rex = petRepository.save(Pet.builder()
                    .nome("Rex")
                    .especie(EspeciePet.CAO)
                    .raca("Golden Retriever")
                    .dataNascimento(LocalDate.now().minusYears(4))
                    .pesoKg(new BigDecimal("28.50"))
                    .sexo(SexoPet.M)
                    .condicoesCronicas("Tendência a obesidade")
                    .ativo(true)
                    .scoreAtual(76)
                    .responsavel(kelson)
                    .clinica(clyvo)
                    .build());

            Pet nina = petRepository.save(Pet.builder()
                    .nome("Nina")
                    .especie(EspeciePet.GATO)
                    .raca("SRD")
                    .dataNascimento(LocalDate.now().minusYears(2))
                    .pesoKg(new BigDecimal("4.20"))
                    .sexo(SexoPet.F)
                    .condicoesCronicas("Sem condições crônicas")
                    .ativo(true)
                    .scoreAtual(92)
                    .responsavel(ana)
                    .clinica(clyvo)
                    .build());

            consultaRepository.save(Consulta.builder()
                    .pet(rex)
                    .clinica(clyvo)
                    .dataConsulta(LocalDateTime.now().plusDays(15))
                    .tipo(TipoConsulta.CHECKUP)
                    .observacoes("Check-up preventivo agendado")
                    .valor(new BigDecimal("180.00"))
                    .build());

            protocoloRepository.save(ProtocoloPreventivo.builder()
                    .especie(EspeciePet.CAO)
                    .tipo(TipoEventoPreventivo.VACINA)
                    .nome("V10")
                    .descricao("Vacina polivalente anual para cães")
                    .idadeMesesAplicacao(2)
                    .intervaloReforcoDias(365)
                    .build());

            protocoloRepository.save(ProtocoloPreventivo.builder()
                    .especie(EspeciePet.GATO)
                    .tipo(TipoEventoPreventivo.VACINA)
                    .nome("V4")
                    .descricao("Vacina polivalente anual para gatos")
                    .idadeMesesAplicacao(2)
                    .intervaloReforcoDias(365)
                    .build());

            eventoRepository.save(EventoPreventivo.builder()
                    .pet(rex)
                    .tipo(TipoEventoPreventivo.CHECKUP)
                    .descricao("Check-up preventivo trimestral")
                    .dataPrevista(LocalDate.now().plusDays(15))
                    .realizado(false)
                    .build());

            leituraColeiraRepository.save(LeituraColeira.builder()
                    .pet(rex)
                    .statusAtividade(StatusAtividade.ATIVO)
                    .nivelBateria(18)
                    .timestampLeitura(LocalDateTime.now().minusHours(1))
                    .build());

            leituraComedouroRepository.save(LeituraComedouro.builder()
                    .pet(rex)
                    .nivelRacaoPct(15)
                    .pesoConsumidoG(new BigDecimal("25.00"))
                    .timestampLeitura(LocalDateTime.now().minusMinutes(40))
                    .build());

            leituraAmbienteRepository.save(LeituraAmbiente.builder()
                    .pet(rex)
                    .temperaturaAmbiente(new BigDecimal("24.50"))
                    .umidadePct(55)
                    .qualidadeArPpm(850)
                    .petPresente(true)
                    .timestampLeitura(LocalDateTime.now().minusMinutes(30))
                    .build());

            alertaRepository.save(AlertaSaude.builder()
                    .pet(rex)
                    .tipo(TipoAlerta.BATERIA_COLEIRA_BAIXA)
                    .nivel(NivelAlerta.MEDIO)
                    .mensagem("Bateria da coleira abaixo de 20%")
                    .resolvido(false)
                    .build());

            scoreRepository.save(ScoreSaude.builder()
                    .pet(rex)
                    .score(76)
                    .categoria(CategoriaScore.AMARELO)
                    .observacao("Score inicial com alerta de bateria e ração baixa.")
                    .build());

            scoreRepository.save(ScoreSaude.builder()
                    .pet(nina)
                    .score(92)
                    .categoria(CategoriaScore.VERDE)
                    .observacao("Score inicial saudável.")
                    .build());
        };
    }
}
