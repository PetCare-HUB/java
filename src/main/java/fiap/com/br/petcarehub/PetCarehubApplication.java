package fiap.com.br.petcarehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@SpringBootApplication
@ConfigurationPropertiesScan
public class PetCarehubApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetCarehubApplication.class, args);
    }
}
