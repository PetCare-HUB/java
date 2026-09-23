package fiap.com.br.petcarehub.keepalive;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KeepAliveService {

    private final RestClient restClient = RestClient.create();

    @Scheduled(fixedRate = 60000)
    public void keepAlive() {

        try {
            restClient.get()
                    .uri("https://petcare-hub-gokt.onrender.com/keepalive")
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {
            System.err.println("Erro no Keep Alive: " + e.getMessage());
        }
    }
}
