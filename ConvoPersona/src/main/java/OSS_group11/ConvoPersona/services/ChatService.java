package OSS_group11.ConvoPersona.services;

import OSS_group11.ConvoPersona.domain.Chat;
import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.domain.Sender;
import OSS_group11.ConvoPersona.dtos.AddChatResDTO;
import OSS_group11.ConvoPersona.dtos.GetChatLogDTO;
import OSS_group11.ConvoPersona.dtos.MbtiPredictionOutputDTO;
import OSS_group11.ConvoPersona.repositories.ChatRepository;
import OSS_group11.ConvoPersona.repositories.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {


    private final FastApiService fastApiService;
    private final ChatGptService chatGptService;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
    public ChatService(FastApiService fastApiService, ChatGptService chatGptService,
                       ChatRepository chatRepository, MessageRepository messageRepository) {
        this.fastApiService = fastApiService;
        this.chatGptService = chatGptService;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    /***
     * 사용자 id로 chatList 불러오기
     * @param memberId
     * @return
     */

    public GetChatLogDTO getChats(Long memberId) {
        List<Chat> chatList = chatRepository.findAllByMemberId(memberId);
        // Demo버전에선 chatList에서 하나의 chat만 있는 걸로 가정
        if (chatList.isEmpty()) {
            return null;
        }
        List<Message> messageList = chatList.get(0).getMessages();
        GetChatLogDTO getChatLogDTO = new GetChatLogDTO(chatList.get(0).getChatId(), messageList);
        return getChatLogDTO;
    }


    /***
     * 0. chatId는 Demo 때는 0으로 고정 (사용자는 하나의 Chat을 가진다고 가정)
     * 1. userPrompt -> Fast API 서버로 요청 -> 예측한 MBTI String을 받아온다.
     * 2. 받아온 MBTI와 userPrompt를 SystemPrompt와 함께 requestBody에 담아 ChatGPT API호출 -> GPT 답변 받아온다.
     * @param chatId
     * @param userPrompt
     * @return
     * @throws JsonProcessingException
     */
    public AddChatResDTO getGptMessage(Long chatId, String userPrompt) throws JsonProcessingException {
        /*
            To Do :
            userMessage DB에 저장해야한다. (completed)
            gptMessage DB에 저장해야한다. (completed)
         */

        System.out.println("chatId = " + chatId);

        //사용자가 입력한 새 prompt 추가 저장하기.
        Chat chat = chatRepository.findById(chatId).orElse(null);
        // chatId로 조회, 없으면 null 반환
        if (chat == null) return null;
        Message userMessage = Message.builder()
                .chat(chat)
                .content(userPrompt)
                .sender(Sender.USER)
                .build();
        Message savedUserMessage = messageRepository.save(userMessage);


        // 사용자의 prompt 불러오기 (사용자가 막 입력한 userPrompt도 포함)
        // 메시지 내용을 담을 List<String> 생성
        /*
        List<String> userPromptList = new ArrayList<>();
        userPromptList.add("안녕");
        userPromptList.add("내 이름은 김도균이라고 해");
        userPromptList.add("요즘 여자친구랑 많이 만나지 못해서 서운한 것 같아");
        userPromptList.add("근데 여자친구는 날 만나고 싶어하지 않는 것 같아서 더욱 심란해");
        userPromptList.add(userPrompt);
        */
        List<String> userPromptLog = messageRepository.findBySenderOrderByIdAsc(Sender.USER)
                .stream()
                .map(Message::getContent) // 각 메시지의 내용(content)을 추출하여 맵핑
                .toList(); // 맵핑된 내용을 리스트로 변환하여 반환


        // FastApiService를 이용해서, userPrompt로 MBTI를 예측한 결과를 받아온다.
        MbtiPredictionOutputDTO mbtiPredictionOutputDTO = fastApiService.predictMbti(userPromptLog);

        System.out.println("mbti = " + mbtiPredictionOutputDTO.getMbti());

        //ChatGPT API 호출 -> mbti + userPrompt requestBody에 담아서 요청 보내서 gptPrompt 받아온다.
        //gptResponse는 Json문자열임, gpt의 답변말고도 여러 정보 포함되어있음.
        // Json 문자열을 처리해서, gpt 답변(gptPrompt) 추출
        String gptResponse = chatGptService.callChatGPTAPI(mbtiPredictionOutputDTO.getMbti(), userPrompt);
        JsonNode gptPromptJson = objectMapper.readTree(gptResponse);
        String gptPrompt = gptPromptJson.get("choices").get(0).get("message").get("content").asText();
        System.out.println("gptPrompt = " + gptPrompt);

        //gptMessage도 message테이블에 저장하기
        Message gptMessage = Message.builder()
                .chat(chat)
                .sender(Sender.GPT)
                .content(gptPrompt)
                .build();
        Message savedGptMessage = messageRepository.save(gptMessage);

        //Entity 자체를 DTO로 넘겨주지 않고, 새 Message 객체 만들어서 넘겨준다.
        Message tempUserMessage = Message.builder()
                .id(savedUserMessage.getId())
                .sender(savedUserMessage.getSender())
                .content(savedUserMessage.getContent())
                .build();

        Message tempGptMessage = Message.builder()
                .id(savedGptMessage.getId())
                .sender(savedGptMessage.getSender())
                .content(savedGptMessage.getContent())
                .build();

        return new AddChatResDTO(tempUserMessage, tempGptMessage);
    }


}
