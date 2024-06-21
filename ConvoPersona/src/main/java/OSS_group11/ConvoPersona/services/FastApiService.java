package OSS_group11.ConvoPersona.services;

import OSS_group11.ConvoPersona.dtos.MbtiPredictionInputDTO;
import OSS_group11.ConvoPersona.dtos.MbtiPredictionOutputDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class FastApiService {
    private final WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public FastApiService(WebClient.Builder webClientBuilder, @Value("${fastapi.server.url}") String serverUrl) {
        this.webClient = webClientBuilder.baseUrl(serverUrl).build();
    }


//     public MbtiPredictionOutputDTO predictMbti(List<String> userPromptList) throws JsonProcessingException {
// //        System.out.println("serverUrl = " + serverUrl);

//         // FastAPI 서버에 요청을 보내고 mbti 문자열을 받아옴
//         return webClient.post()
//                 .uri("/mbti_prediction") // FastAPI 서버의 엔드포인트 지정
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .bodyValue(objectMapper.writeValueAsString(new MbtiPredictionInputDTO(userPromptList)))
//                 .retrieve()
//                 .bodyToMono(MbtiPredictionOutputDTO.class)
//                 .block();
//     }

    public MbtiPredictionOutputDTO predictMbti(List<String> userPromptList) {
        try {
            return webClient.post()
                    .uri("/mbti_prediction") // FastAPI 서버의 엔드포인트 지정
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new MbtiPredictionInputDTO(userPromptList))
                    .retrieve()
                    .bodyToMono(MbtiPredictionOutputDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            // 서버로부터 받은 오류 응답 처리
            System.err.println("Error response from FastAPI server: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get response from FastAPI server", e);
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("Error during API call: " + e.getMessage());
            throw new RuntimeException("Error during API call", e);
        }
    }
}
