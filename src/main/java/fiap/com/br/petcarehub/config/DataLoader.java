package fiap.com.br.petcarehub.config;

import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.entity.Tutor;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import fiap.com.br.petcarehub.repository.TutorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    // So roda se app.seed.enabled=true (ligado por padrao em application.properties).
    // Nao insere nada se ja existir alguma clinica - seguro de deixar ligado
    // mesmo apontando pro banco de producao, so serve pra destravar o
    // primeiro acesso quando o schema esta genuinamente vazio.
    @Bean
    @ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
    CommandLineRunner seedDatabase(
            ClinicaRepository clinicaRepository,
            TutorRepository tutorRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (clinicaRepository.count() > 0) {
                return;
            }

            Clinica clinica = clinicaRepository.save(Clinica.builder()
                    .nome("Clinica Vida Pet")
                    .email("contato@clinicavidapet.test")
                    .senha(passwordEncoder.encode("Senha123"))
                    .cnpj("11111111000111")
                    .endereco("Av. Paulista, 1000 - Sao Paulo")
                    .telefone("1133334444")
                    .ativo('S')
                    .build());

            tutorRepository.save(Tutor.builder()
                    .nome("Tutor Teste")
                    .email("tutor.teste@petcarehub.test")
                    .telefone("11999990000")
                    .cpf("12345678901")
                    .statusAcesso(StatusAcesso.PRE_CADASTRADO)
                    .clinica(clinica)
                    .build());
        };
    }
}
