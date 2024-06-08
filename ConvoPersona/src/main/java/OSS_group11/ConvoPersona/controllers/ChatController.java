package OSS_group11.ConvoPersona.controllers;

import OSS_group11.ConvoPersona.dtos.AddChatReqDTO;
import OSS_group11.ConvoPersona.dtos.AddChatResDTO;
import OSS_group11.ConvoPersona.dtos.GetChatLogDTO;
import OSS_group11.ConvoPersona.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /***
     * user chatLog 반환해주기
     * 유저가 작성했던 prompt 조회
     * Chat 객체 넘겨주기
     * @param memberId
     * @return GetChatLogDTO -> (chatId, messages)
     */
    @GetMapping("/chat")
    @CrossOrigin(origins = "https://convo-persona.netlify.app")
    public ResponseEntity<GetChatLogDTO> getChatLog(@RequestHeader("Authorization") Long memberId) {
//        System.out.println("--------------------------");
//        System.out.println("memberId = " + memberId);
        GetChatLogDTO getChatLogDTO = chatService.getChats(memberId);
//        System.out.println("채팅 이력 불러오기 GetMapping 성공");
        return ResponseEntity.ok(getChatLogDTO);
        //{chatId, List<Message> messageList} 반환
        // message객체(id, sender, content) 리스트로 응답하기(시간 순대로 저장되어 있을거임)
    }

    /***
     * 유저가 prompt 작성해서 request 보낸 경우
     * user prompt 받아서 처리
     * AddChatResDTO 넘겨주기 (userMessage, gptMessage)
     * @param memberId
     * @param addChatReqDTO
     * @return
     */
    @PostMapping("/chat")
    @CrossOrigin(origins = "https://convo-persona.netlify.app")
    public ResponseEntity<?> postUserPrompt(@RequestHeader("Authorization") Long memberId, @RequestBody AddChatReqDTO addChatReqDTO) throws Exception {

        // 복호화해서 사용

        // Demo 이후에는 POST "/chat/{chatId}"으로 수정해야함
        // -> 일단 chatId는 0으로 지정해줄 것(Member당 Chat이 1개인 상황이기 때문)
        // chatId를 가지는 Chat이 있는지 확인한다. 없으면 Exception 발생시키기.
        Long chatId = chatService.getChatId(memberId);


        // chatService -> 사용자가 보낸 userPrompt 기반, GPT 답변 받아오기
        // addChatReqDTO.getContent() : 프론트 쪽에서 사용자 대화내용을 암호화를 해서 전송한다
        AddChatResDTO addChatResDTO = chatService.getGptMessage(chatId, addChatReqDTO.getContent());

        if (addChatResDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chatId 존재 X");
        }
        return ResponseEntity.ok(addChatResDTO);
    }

    /***
     * 유저가 채팅창에서 새로고침 버튼 누르면,
     * 채팅 내역이 DB에 백업되고,
     * 채팅 기록은 삭제된다.
     * @param memberId
     */
    @DeleteMapping("/chat")
    @CrossOrigin(origins = "https://convo-persona.netlify.app")
    public void deleteChatLog(@RequestHeader("Authorization") Long memberId) {
        chatService.backupAndDeleteChat(memberId);
    }

}