package OSS_group11.ConvoPersona.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TranslationService {
    @Value("${chatgpt.api.key}")
    private String apiKey;

    private final WebClient webClient;

    @Autowired
    public TranslationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .build();
    }


    public String translateToEnglish(String userPrompt) throws JsonProcessingException {
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Translate the following Korean text to English");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("model", "gpt-3.5-turbo");
        requestBodyMap.put("messages", List.of(systemMessage, userMessage));

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        //chatGPT API 요청 보낼 때, requestBody 테스트
        System.out.println("requestBody = " + requestBody);

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
