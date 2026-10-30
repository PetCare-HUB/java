package fiap.com.br.petcarehub.config;

import fiap.com.br.petcarehub.entity.Admin;
import fiap.com.br.petcarehub.entity.Clinica;
import fiap.com.br.petcarehub.entity.Tutor;
import fiap.com.br.petcarehub.enums.StatusAcesso;
import fiap.com.br.petcarehub.repository.AdminRepository;
import fiap.com.br.petcarehub.repository.ClinicaRepository;
import fiap.com.br.petcarehub.repository.TutorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Slf4j
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
                    .statusAcesso(StatusAcesso.ATIVO)
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

    /**
     * Cria o primeiro admin da PetCare Hub (quem aprova as clinicas).
     *
     * De proposito NAO tem senha padrao: se ADMIN_EMAIL/ADMIN_PASSWORD nao
     * estiverem no ambiente, nenhum admin e criado. Uma credencial default
     * versionada no repositorio seria a conta mais privilegiada do sistema
     * com senha publica.
     */
    @Bean
    CommandLineRunner seedAdmin(
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.admin-email:}") String adminEmail,
            @Value("${app.seed.admin-password:}") String adminPassword,
            @Value("${app.seed.admin-nome:Admin PetCare Hub}") String adminNome
    ) {
        return args -> {
            if (adminRepository.count() > 0) {
                return;
            }
            if (adminEmail.isBlank() || adminPassword.isBlank()) {
                log.warn("Nenhum admin cadastrado e ADMIN_EMAIL/ADMIN_PASSWORD não configurados. "
                        + "O credenciamento de clínicas (/admin/**) fica indisponível até criar um admin.");
                return;
            }

            adminRepository.save(Admin.builder()
                    .nome(adminNome)
                    .email(adminEmail.trim().toLowerCase(Locale.ROOT))
                    .senha(passwordEncoder.encode(adminPassword))
                    .ativo('S')
                    .build());
            log.info("Admin inicial da PetCare Hub criado: {}", adminEmail);
        };
    }
}
