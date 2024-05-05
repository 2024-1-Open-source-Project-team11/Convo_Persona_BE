package OSS_group11.ConvoPersona.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
                .build();
    }

    public String callChatGPTAPI(String mbti, String userPrompt) {
        // 사용자 메시지와 시스템 프롬프트를 포함한 요청 본문 생성

        String requestBody = """
                {
                    "model": "gpt-3.5-turbo",
                    "messages": [
                        {"role": "system", "content": "사용자의 MBTI는 %s이야. 이에 맞게 너는 맞춤 상담사가 되어줘야해"},
                        {"role": "user", "content": "%s"}
                    ]
                }
                """.formatted(mbti, userPrompt);

        System.out.println("requestBody = \n" + requestBody);
        System.out.println();

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
