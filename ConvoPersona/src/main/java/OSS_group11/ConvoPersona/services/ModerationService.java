package OSS_group11.ConvoPersona.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ModerationService {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    private final WebClient webClient;

    @Autowired
    public ModerationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1/moderations")
                .build();
    }

    private final Map<String, Double> categoryThresholds = Map.of(
            "violence", 0.05,
            "hate", 0.001,
            "self-harm", 0.04,
            "self-harm/intent", 0.05,
            "self-harm/instructions", 0.05,
            "harassment", 0.05
    );

    public Mono<Boolean> moderateText(String userPrompt) {
        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(Map.of("input", userPrompt))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

                    System.out.println("results = " + results);

                    Map<String, Object> firstResult = results.get(0);
                    boolean flagged = (boolean) firstResult.get("flagged");
                    Map<String, Double> textScores = (Map<String, Double>) firstResult.get("category_scores");

                    if (!flagged) {
                        return isHarmful(textScores);
                    }
                    return true;
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // 예외 처리 로직
                    ex.printStackTrace();
                    return Mono.just(false);
                });
    }

    private boolean isHarmful(Map<String, Double> textScores) {
        for (Map.Entry<String, Double> entry : categoryThresholds.entrySet()) {
            if (textScores.get(entry.getKey()) >= entry.getValue()) {
                return true;
            }
        }
        return false;
    }
}
