package OSS_group11.ConvoPersona.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ChatGptService {
    private final WebClient webClient;
    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Autowired
    public ChatGptService(WebClient.Builder webClientBuilder) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer YOUR_API_KEY")
                .build();
    }

    public String callChatGPTAPI(String mbti, String userPrompt) {
        // MBTI 유형에 따른 시스템 프롬프트 설정
        String systemPrompt = "";
        switch (mbti) {
            case "INTJ":
                systemPrompt = "As an INTJ, I prefer to...";
                break;
            case "ENTP":
                systemPrompt = "Being an ENTP, I'd say...";
                break;
            // 다른 MBTI 유형에 대한 케이스 추가
            default:
                systemPrompt = "As an individual, I think...";
                break;
        }

        // 사용자 메시지와 시스템 프롬프트를 포함한 요청 본문 생성

        String requestBody = """
                {
                    "model": "gpt-3.5-turbo",
                    "prompt": "%s",
                    "messages": [
                        {"role": "system", "content": "%s"}
                    ]
                }
                """.formatted(userPrompt, systemPrompt);

        return webClient.post()
                .uri("/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
