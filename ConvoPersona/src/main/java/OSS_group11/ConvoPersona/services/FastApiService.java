package OSS_group11.ConvoPersona.services;

import OSS_group11.ConvoPersona.dtos.MbtiPredictionInputDTO;
import OSS_group11.ConvoPersona.dtos.MbtiPredictionOutputDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class FastApiService {
    private final WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${fastapi.server.url}")
    private String serverUrl;

    @Autowired
    public FastApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(serverUrl).build();
    }


    public MbtiPredictionOutputDTO predictMbti(List<String> userPromptList) throws JsonProcessingException {


        // FastAPI 서버에 요청을 보내고 mbti 문자열을 받아옴
        return webClient.post()
                .uri("/mbti_prediction") // FastAPI 서버의 엔드포인트 지정
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new MbtiPredictionInputDTO(userPromptList)))
                .retrieve()
                .bodyToMono(MbtiPredictionOutputDTO.class)
                .block();
    }
}
