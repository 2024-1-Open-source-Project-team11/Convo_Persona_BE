package OSS_group11.ConvoPersona.services;

import OSS_group11.ConvoPersona.domain.*;
import OSS_group11.ConvoPersona.domain.archive.ArchivedChat;
import OSS_group11.ConvoPersona.domain.archive.ArchivedFeedback;
import OSS_group11.ConvoPersona.domain.archive.ArchivedMessage;
import OSS_group11.ConvoPersona.dtos.AddChatResDTO;
import OSS_group11.ConvoPersona.dtos.GetChatLogDTO;
import OSS_group11.ConvoPersona.dtos.MbtiPredictionOutputDTO;
import OSS_group11.ConvoPersona.repositories.ChatRepository;
import OSS_group11.ConvoPersona.repositories.FeedbackRepository;
import OSS_group11.ConvoPersona.repositories.MemberRepository;
import OSS_group11.ConvoPersona.repositories.MessageRepository;
import OSS_group11.ConvoPersona.repositories.archive.ArchivedChatRepository;
import OSS_group11.ConvoPersona.repositories.archive.ArchivedFeedbackRepository;
import OSS_group11.ConvoPersona.repositories.archive.ArchivedMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {

    private final FastApiService fastApiService;
    private final ChatGptService chatGptService;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final FeedbackRepository feedbackRepository;
    private final ArchivedChatRepository archivedChatRepository;
    private final ArchivedMessageRepository archivedMessageRepository;
    private final ArchivedFeedbackRepository archivedFeedbackRepository;
    private final EncryptionService encryptionService;
    private final TranslationService translationService;
    private final ModerationService moderationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatService(FastApiService fastApiService, ChatGptService chatGptService,
                       ChatRepository chatRepository, MessageRepository messageRepository,
                       MemberRepository memberRepository, FeedbackRepository feedbackRepository,
                       ArchivedChatRepository archivedChatRepository,
                       ArchivedMessageRepository archivedMessageRepository,
                       ArchivedFeedbackRepository archivedFeedbackRepository,
                       EncryptionService encryptionService, TranslationService translationService,
                       ModerationService moderationService) {
        this.fastApiService = fastApiService;
        this.chatGptService = chatGptService;
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.memberRepository = memberRepository;
        this.feedbackRepository = feedbackRepository;
        this.archivedChatRepository = archivedChatRepository;
        this.archivedMessageRepository = archivedMessageRepository;
        this.archivedFeedbackRepository = archivedFeedbackRepository;
        this.encryptionService = encryptionService;
        this.translationService = translationService;
        this.moderationService = moderationService;
    }

    /***
     * 사용자 id로 chatList 불러오기
     * @param memberId
     * @return
     */
    public GetChatLogDTO getChats(Long memberId) {
//        System.out.println("ChatService.getChats() 진입 성공");
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
//        for (Message message : tempMessageList) {
//            System.out.println("message = " + message.getContent());
//        }
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
    public AddChatResDTO getGptMessage(Long chatId, String userPrompt) throws Exception {

        //사용자가 입력한 새 prompt 추가 저장하기.
        Chat chat = chatRepository.findById(chatId).orElse(null);
        // chatId로 조회, 없으면 null 반환
        if (chat == null) return null;

        // userPrompt 영어로 번역하고,
        String translateResponse = translationService.translateToEnglish(encryptionService.decrypt(userPrompt));
        JsonNode translateResponseJson = objectMapper.readTree(translateResponse);
        String english_userPrompt = translateResponseJson.get("choices").get(0).get("message").get("content").asText();


        System.out.println("english_userPrompt = " + english_userPrompt);

        // moderation API를 이용해 유해성 검증한다.
        Boolean isModerationBlocked = moderationService.moderateText(english_userPrompt).block();
        String moderationResult = (isModerationBlocked != null) ? isModerationBlocked.toString() : "unknown";
        System.out.println("moderationResult = " + moderationResult);     //moderation 결과 출력
        if (Boolean.TRUE.equals(isModerationBlocked)) {
            Message userMessage = Message.builder()
                    .chat(chat)
                    .mbti(Mbti.UNDEFINED)
                    .sender(Sender.USER)
                    .content(encryptionService.encrypt(userPrompt))
                    .build();

            Message gptMessage = Message.builder()
                    .chat(chat)
                    .sender(Sender.GPT)
                    .content(encryptionService.encrypt("유해한 내용이 감지되었습니다."))
                    .build();

            return new AddChatResDTO(userMessage, gptMessage);
        }


        String historyChat = getHistoryMessages(chatId);


        Message userMessage = Message.builder()
                .chat(chat)
                .content(userPrompt)
                .sender(Sender.USER)
                .build();
        Message savedUserMessage = messageRepository.save(userMessage);


        //Sender.USER로 검색하면 안 되고, memberId + Sender.USER로 검색해야함.
        //하지만, 나중에는 한 멤버가 여러 채팅을 가질 수 있기 때문에, 해당 로직 반영해야한다
        /*
            FastAPI 서버로 요청보낼 때, 복호화해서 요청보내기
        */
        //FastAPI로 요청보낼 때 복호화 한 userPrompt

//        System.out.println();
//        System.out.println("FastAPI로 요청보낼 때 복호화 한 userPrompt : " + encryptionService.encrypt(userPrompt));
//        System.out.println("FastAPI로 요청보낼 때 복호화 한 userPrompt : " + encryptionService.decrypt(userPrompt));
//
//        System.out.println("FastAPI로 요청보낼 때 복호화 한 userPrompt : " + encryptionService.encrypt(encryptionService.encrypt(userPrompt)));
//        System.out.println();

        List<String> userPromptLog = messageRepository.findByChatAndSenderOrderByIdAsc(chat, Sender.USER)
                .stream()
                .map(message -> {
                    try {
                        return encryptionService.decrypt(message.getContent()); //복호화
                    } catch (Exception e) {
                        // 복호화에 실패한 경우 처리
                        e.printStackTrace();
                        return null; // 또는 다른 값을 반환하거나 예외를 throw할 수 있음
                    }
                })
                .toList(); // 맵핑된 내용을 리스트로 변환하여 반환

//        System.out.println("userPromptLog 보여주기 --------------------------------");
//
//        for (String log : userPromptLog) {
//            System.out.println(log);
//            System.out.println(log + ": " + encryptionService.decrypt(log));
//        }

        // FastApiService를 이용해서, userPrompt로 MBTI를 예측한 결과를 받아온다.
        MbtiPredictionOutputDTO mbtiPredictionOutputDTO = fastApiService.predictMbti(userPromptLog);

        //받아온 MBTI예측값으로 savedUserMessage의 MBTI 값 update
        savedUserMessage.updateMbti(mbtiPredictionOutputDTO.getMbti());

        // ChatGPT API 호출 -> mbti + userPrompt requestBody에 담아서 요청 보내서 gptPrompt 받아온다.
        // gptResponse는 Json문자열임, gpt의 답변말고도 여러 정보 포함되어있음.
        // Json 문자열을 처리해서, gpt 답변(gptPrompt) 추출
        /*
            chatGPT API로 요청보낼 땐 복호화
        */
        System.out.println("chatGPT API로 요청보낼 때 암호화 한 userPrompt : " + encryptionService.decrypt(userPrompt));
        String gptResponse = chatGptService.callChatGPTAPI(mbtiPredictionOutputDTO.getMbti(),
                encryptionService.decrypt(userPrompt), historyChat);
        JsonNode gptPromptJson = objectMapper.readTree(gptResponse);
        String gptPrompt = gptPromptJson.get("choices").get(0).get("message").get("content").asText();
//        System.out.println("gptPrompt = " + gptPrompt);

        //gptMessage도 message테이블에 저장하기
        /*
            gptPrompt DB에 저장할 때, 암호화해서 저장하기
        */
        Message gptMessage = Message.builder()
                .chat(chat)
                .sender(Sender.GPT)
                .content(encryptionService.encrypt(gptPrompt))
                .mbti(Mbti.UNDEFINED)
                .build();
        Message savedGptMessage = messageRepository.save(gptMessage);

        return new AddChatResDTO(savedUserMessage, savedGptMessage);

        //순환참조 문제를 막기 위해,
        //Entity 자체를 DTO로 넘겨주지 않고, 새 Message 객체 만들어서 넘겨준다.
        //@JsonManagedReference + @JsonBackReference로 해결함. 아래 코드는 의미없는듯? DTO에 담아서 보내니까
        /*
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
        */


    }

    /***
     * chatGPT API에 요청보낼 때 system prompt에 담길 상담내용 가공
     * 대화내용 DB에서 조회해서, 형식에 맞게 문자열 생성
     * @param chatId
     * @return
     */
    private String getHistoryMessages(Long chatId) throws Exception {
        String historyMessage = "";


        // 대화내용 DB에서 가져오기
        Chat chat = chatRepository.findById(chatId).get();
        List<Message> allMessages = messageRepository.findByChatOrderByCreatedAtAsc(chat);

        String title = "대화 기록\n";
        historyMessage += title;

        //대화내용이 없으면 빈 문자열 리턴
        if (allMessages.isEmpty()) return "";

        for (Message message : allMessages) {
            if (message.getSender() == Sender.USER) {
                historyMessage += "USER : " + encryptionService.decrypt(message.getContent()) + "\n";
            } else {
                //Sender.GPT일 때
                historyMessage += "GPT : " + encryptionService.decrypt(message.getContent()) + "\n";
            }
        }

        System.out.println("historyMessage: " + historyMessage + "\n\n");

        return historyMessage;
    }

    /***
     * Message 데이터를 삭제하기 전에, 백업용 테이블에 저장한 뒤에 삭제한다.
     * @param memberId
     */
    public void deleteAllMessage(Long memberId) {
        Optional<Chat> chat = chatRepository.findByMemberId(memberId);
        Optional<Member> member = memberRepository.findById(memberId);

        Long chatId = chat.get().getChatId();

        chatRepository.deleteById(chatId);      //chat을 삭제하면 해당 chat에 속한 메시지들도 모두 삭제된다.

        chatRepository.save(Chat.builder()
                .member(member.get())
                .build());
    }

    /***
     * Message 데이터를 삭제하기 전에, 백업용 테이블에 저장한 뒤에 삭제한다.
     * @param memberId
     */
    public void backupAndDeleteChat(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("member doesn't exist"));

        Chat chat = chatRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("chat doesn't exist"));

        // 사용자 대화기록이 없으면 백업할 것도 없기 때문에, 막아야한다.
        // Otherwise, 새로고침 누를 때마다 chatId가 계속 archived_chat테이블에 계속 쌓임
        List<Message> messages = chat.getMessages();
        if (messages.isEmpty()) {
            return;
        }

        ArchivedChat archivedChat = ArchivedChat.builder()
                .member(member)
                .build();

        for (Message message : chat.getMessages()) {
            ArchivedMessage archivedMessage = ArchivedMessage.builder()
                    .archivedChat(archivedChat)
                    .content(message.getContent())
                    .createdAt(message.getCreatedAt())
                    .mbti(message.getMbti())
                    .build();
            if (message.getFeedback() != null) {
                //message가 feedback을 갖고 있다면, 해당 feedback 백업

                ArchivedFeedback archivedFeedback = ArchivedFeedback.builder()
                        .archivedMessage(archivedMessage)
                        .content(message.getFeedback().getContent())
                        .member(member)
                        .build();
                archivedFeedbackRepository.save(archivedFeedback);
            }

            archivedMessageRepository.save(archivedMessage);
        }

        archivedChatRepository.save(archivedChat);

        //삭제하기
        chatRepository.deleteById(chat.getChatId());

        //chat 삭제해서 하나도 없게 되면, 채팅 리스트 띄울 때 에러가 난다.
        chatRepository.save(Chat.builder()
                .member(member)
                .build());
    }
}
