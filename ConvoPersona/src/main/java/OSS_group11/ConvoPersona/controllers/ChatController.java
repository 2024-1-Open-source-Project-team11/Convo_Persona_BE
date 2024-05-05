package OSS_group11.ConvoPersona.controllers;

import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.domain.Sender;
import OSS_group11.ConvoPersona.dtos.AddChatReqDTO;
import OSS_group11.ConvoPersona.dtos.AddChatResDTO;
import OSS_group11.ConvoPersona.services.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param userId
     * @return
     */
//    @GetMapping("/chat/{userId}")
//    public Chat getChatLog(@PathVariable long userId) {
//        List<Chat> chatList = chatService.getChats(userId);
//        return chatList.get(0); //message 객체(id, sender, content) 리스트로 응답하기(시간 순대로 저장되어 있을거임)
//    }

    /***
     * 유저가 prompt 작성해서 request 보낸 경우
     * user prompt 받아서 처리
     * AddChatResDTO 넘겨주기 (userMessage, gptMessage)
     * @param userId
     * @param addChatReqDTO
     * @return
     */
    @PostMapping("/chat")
    public AddChatResDTO postUserPrompt(@RequestHeader("authorization") Long userId, @RequestBody AddChatReqDTO addChatReqDTO) throws JsonProcessingException {
        System.out.println("userId = " + userId);
        System.out.println("addChatReqDTO = " + addChatReqDTO.getContent());

        // 사용자가 입력한 userPrompt (=addChatReqDTO.getContent()) 이용
        // Message Entity DB에 저장하기
        Message userMessage = new Message(1, Sender.USER, addChatReqDTO.getContent());

        // chatService -> 사용자가 보낸 userPrompt 기반, GPT 답변 받아오기
        Message gptMessage = chatService.getGptMessage(addChatReqDTO.getContent());
        System.out.println("gptMessage = " + gptMessage.getContent());
        return new AddChatResDTO(userMessage, gptMessage);
    }

//    @PostMapping("/user/{userId}/prompt")
//    public ResponseEntity<String> savePrompt(@PathVariable Long userId, @RequestBody String prompt) {
//        chatService.savePrompt(userId, prompt);
//        return new ResponseEntity<>(prompt, HttpStatus.OK);
//    }

}