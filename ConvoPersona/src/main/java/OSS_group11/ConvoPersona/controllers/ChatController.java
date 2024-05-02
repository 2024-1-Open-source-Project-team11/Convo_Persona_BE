package OSS_group11.ConvoPersona.controllers;

import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.domain.Sender;
import OSS_group11.ConvoPersona.dtos.AddChatReqDTO;
import OSS_group11.ConvoPersona.dtos.AddChatResDTO;
import OSS_group11.ConvoPersona.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /***
     * user chatLog 반환해주기
     * 유저가 작성했던 prompt 조회
     * Message 객체 리스트 넘겨주기
     * @param userId
     * @return
     */
//    @GetMapping("/chats/{userId}")
//    public List<Message> getChatLog(@PathVariable long userId) {
//        List<Message> messageList = chatService.getChats(userId);
//        return messageList; //message 객체(id, sender, content) 리스트로 응답하기(시간 순대로 저장되어 있을거임)
//    }

    /***
     * 유저가 prompt 작성해서 request 보낸 경우
     * user prompt 받아서 처리
     * AddChatResDTO 넘겨주기 (userMessage, gptMessage)
     * @param userId
     * @param addChatReqDTO
     * @return
     */
    @PostMapping("/chats")
    public AddChatResDTO postUserPrompt(@RequestHeader("authorization") Long userId, @RequestBody AddChatReqDTO addChatReqDTO) {
        System.out.println("userId = " + userId);
        System.out.println("addChatReqDTO = " + addChatReqDTO.getContent());


        Message userMessage = new Message(1, Sender.USER, addChatReqDTO.getContent());
        Message gptMessage = chatService.getGptMessage(addChatReqDTO.getContent());
        return new AddChatResDTO(userMessage, gptMessage);
    }

//    @PostMapping("/user/{userId}/prompt")
//    public ResponseEntity<String> savePrompt(@PathVariable Long userId, @RequestBody String prompt) {
//        chatService.savePrompt(userId, prompt);
//        return new ResponseEntity<>(prompt, HttpStatus.OK);
//    }

}