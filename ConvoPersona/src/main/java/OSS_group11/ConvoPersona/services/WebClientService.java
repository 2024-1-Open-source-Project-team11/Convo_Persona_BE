package OSS_group11.ConvoPersona.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {
    private final WebClient webClient;

    @Autowired
    public WebClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://your-fastapi-server-url").build();
    }

    public Mono<String> getMbti(String userPrompt) {
        // FastAPI 서버에 요청을 보내고 mbti 문자열을 받아옴
        return webClient.post()
                .uri("/your-endpoint") // FastAPI 서버의 엔드포인트 지정
                .bodyValue(userPrompt)
                .retrieve()
                .bodyToMono(String.class);
    }

    public String getGptPrompt() {
        
    }
}
