package OSS_group11.ConvoPersona.services;

import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.domain.Sender;
import OSS_group11.ConvoPersona.dtos.MbtiPredictionOutputDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public Message getGptMessage(String userPrompt) throws JsonProcessingException {
        /*
            To Do :
            userMessage DB에 저장해야한다.
            gptMessage DB에 저장해야한다.
         */
        //FastApiService를 이용해서, userPrompt로 MBTI를 예측한 결과를 받아온다.
        List<String> userPromptList = new ArrayList<>();

        userPromptList.add("안녕");
        userPromptList.add("내 이름은 김도균이라고 해");
        userPromptList.add("요즘 여자친구랑 많이 만나지 못해서 서운한 것 같아");
        userPromptList.add("근데 여자친구는 날 만나고 싶어하지 않는 것 같아서 더욱 심란해");
        userPromptList.add(userPrompt);

        MbtiPredictionOutputDTO mbtiPredictionOutputDTO = fastApiService.predictMbti(userPromptList);

        System.out.println("mbti = " + mbtiPredictionOutputDTO.getMbti());

        //ChatGPT API 호출 -> mbti + userPrompt requestBody에 담아서 요청 보내서 gptPrompt 받아온다.
        String gptPrompt = chatGptService.callChatGPTAPI(mbtiPredictionOutputDTO.getMbti(), userPrompt);

        //String gptPrompt_imsi = "imsi";
        return new Message(2, Sender.GPT, gptPrompt);
    }
}
