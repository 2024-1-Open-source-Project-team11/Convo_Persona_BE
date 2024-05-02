package OSS_group11.ConvoPersona.services;

import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.domain.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {


    private final FastApiService fastApiService;
    private final ChatGptService chatGptService;


    @Autowired
    public ChatService(FastApiService fastApiService, ChatGptService chatGptService) {
        this.fastApiService = fastApiService;
        this.chatGptService = chatGptService;
    }

    /***
     * 1. userPrompt -> Fast API 서버로 요청 -> 예측한 MBTI String을 받아온다.
     * 2. 받아온 MBTI와 userPrompt를 SystemPrompt와 함께 requestBody에 담아 ChatGPT API호출 -> GPT 답변 받아온다.
     * @param userPrompt
     * @return
     */
    public Message getGptMessage(String userPrompt) {
        //FastApiService를 이용해서, userPrompt로 MBTI를 예측한 결과를 받아온다.
        String mbti = fastApiService.predictMbti(userPrompt);

        //ChatGPT API 호출 -> mbti + userPrompt requestBody에 담아서 요청 보내서 gptPrompt 받아온다.
        String gptPrompt = chatGptService.callChatGPTAPI(mbti, userPrompt);

        //String gptPrompt_imsi = "imsi";
        return new Message(2, Sender.GPT, gptPrompt);
    }
}
