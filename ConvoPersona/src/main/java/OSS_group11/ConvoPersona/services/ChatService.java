package OSS_group11.ConvoPersona.services;

import OSS_group11.ConvoPersona.domain.Chat;
import OSS_group11.ConvoPersona.domain.Member;
import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.domain.Sender;
import OSS_group11.ConvoPersona.dtos.AddChatResDTO;
import OSS_group11.ConvoPersona.dtos.GetChatLogDTO;
import OSS_group11.ConvoPersona.dtos.MbtiPredictionOutputDTO;
import OSS_group11.ConvoPersona.repositories.ChatRepository;
import OSS_group11.ConvoPersona.repositories.MemberRepository;
import OSS_group11.ConvoPersona.repositories.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {


    private final FastApiService fastApiService;
    private final ChatGptService chatGptService;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ChatService(FastApiService fastApiService, ChatGptService chatGptService,
                       ChatRepository chatRepository, MessageRepository messageRepository,
                       MemberRepository memberRepository) {
        this.fastApiService = fastApiService;
        this.chatGptService = chatGptService;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.memberRepository = memberRepository;
    }


    /***
     * 사용자 id로 chatList 불러오기
     * @param memberId
     * @return
     */
    public GetChatLogDTO getChats(Long memberId) {
        System.out.println("ChatService.getChats() 진입 성공");
        Optional<Chat> chat = chatRepository.findByMemberId(memberId);
        Optional<Member> member = memberRepository.findById(memberId);
        List<Message> tempMessageList = new ArrayList<>();

        //회원의 과거 채팅 이력이 없으면
        //채팅 새로 만들어준다(savedChat)
        if (chat.isEmpty()) {
            Chat savedChat = chatRepository.save(Chat.builder()
                    .member(member.get())
                    .build());

            // 새로 만든 savedChat에는 message가 하나도 없을 것이다.
            // 따라서 messageList에는 null이 저장되어있을 것. 빈 리스트를 반환해준다.
            List<Message> messageList = savedChat.getMessages();

            //빈 문자열인 tempMessageList를 반환해준다.
            return new GetChatLogDTO(savedChat.getChatId(), tempMessageList);
        }

        //회원의 과거 채팅이 있으면 해당 채팅 불러오기
        List<Message> messageList = chat.get().getMessages();
        tempMessageList.addAll(messageList);
        Collections.reverse(tempMessageList);
        for (Message message : tempMessageList) {
            System.out.println("message = " + message.getContent());
        }
        Long chatId = chat.get().getChatId();
        return new GetChatLogDTO(chatId, tempMessageList);
    }

    public Long getChatId(Long memberId) {
        Optional<Chat> chat = chatRepository.findByMemberId(memberId);

        if (chat.isEmpty()) {
            return null;
        }

        return chat.get().getChatId();
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


        //순환참조 문제를 막기 위해,
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


    public void deleteAllMessage(Long memberId) {
        Optional<Chat> chat = chatRepository.findByMemberId(memberId);
        Optional<Member> member = memberRepository.findById(memberId);

        Long chatId = chat.get().getChatId();

        chatRepository.deleteById(chatId);      //chat을 삭제하면 해당 chat에 속한 메시지들도 모두 삭제된다.

        chatRepository.save(Chat.builder()
                .member(member.get())
                .build());
    }
}
