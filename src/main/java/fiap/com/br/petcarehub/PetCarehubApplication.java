package fiap.com.br.petcarehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PetCarehubApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetCarehubApplication.class, args);
    }

}
