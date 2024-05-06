package OSS_group11.ConvoPersona.controllers;

import OSS_group11.ConvoPersona.dtos.AddChatReqDTO;
import OSS_group11.ConvoPersona.dtos.AddChatResDTO;
import OSS_group11.ConvoPersona.dtos.GetChatLogDTO;
import OSS_group11.ConvoPersona.services.ChatService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vi")
public class ChatController {

    private final ChatService chatService;

    ObjectMapper objectMapper = new ObjectMapper();

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
    public GetChatLogDTO getChatLog(@RequestHeader("Authorization") Long memberId) {
        GetChatLogDTO getChatLogDTO = chatService.getChats(memberId);
        return getChatLogDTO;
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
    public ResponseEntity<?> postUserPrompt(@RequestHeader("Authorization") Long memberId, @RequestBody AddChatReqDTO addChatReqDTO) throws JsonProcessingException {
        System.out.println("memberId = " + memberId);
        System.out.println("userPrompt = " + addChatReqDTO.getContent());


        // 사용자가 입력한 userPrompt (=addChatReqDTO.getContent()) 이용
        // Message Entity DB에 저장하기
        //memberId -> chatId -> message 순서대로 저장 (과연 순서대로 저장이 될까?)

        // Demo 이후에는 POST "/chat/{chatId}"으로 수정해야함
        // -> 일단 chatId는 0으로 지정해줄 것(Member당 Chat이 1개인 상황이기 때문)
        // chatId를 가지는 Chat이 있는지 확인한다. 없으면 Exception 발생시키기.
        Long chatId = 1L;

        //Message userMessage = new Message(1L, Sender.USER, addChatReqDTO.getContent());
        // chatService -> 사용자가 보낸 userPrompt 기반, GPT 답변 받아오기
        // response -> {userMessage, gptMessage}, AddChatResDTO로 수정

        AddChatResDTO addChatResDTO = chatService.getGptMessage(chatId, addChatReqDTO.getContent());

        if (addChatResDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("chatId 존재 X");
        }
        System.out.println("ChatController.postUserPrompt");
        System.out.println(objectMapper.writeValueAsString(addChatResDTO));
        return ResponseEntity.ok(addChatResDTO);
    }

}

/*
gptPrompt = {
  "id": "chatcmpl-9LwiEKFItqJAwJtNHOwX01X1vhhrR",
  "object": "chat.completion",
  "created": 1715017606,
  "model": "gpt-3.5-turbo-0125",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "안녕! ESTP 유형인 당신에게 조언을 드릴게. 감정을 표현하는 것이 쉽지 않을 수 있지만, 이를 효과적으로 전달하기 위해서 몇 가지 팁을 준비했어. \n\n1. 직접적이고 솔직하게 표현하기: 당신은 직설적이고 현실적인 성향이 강할 거라고 생각해. 그러니 감정을 담담하고 직접적으로 표현할 필요가 있어.\n\n2. 구체적인 예시와 사례 제시하기: 감정을 설명할 때 구체적인 상황이나 사례를 들어 예시를 들어주면 상대방이 이해하기 쉬울 거야.\n\n3. 진심을 담아 표현하기: 진심이 담긴 어조와 몸짓으로 감정을 전달하면 상대방이 더 잘 이해할 수 있을 거야.\n\n4. 상대방의 시선에 서서 생각해보기: 상대방이 받아들일 수 있는 방식으로 감정을 전달하는 것이 중요해. 그래서 상대방의 입장에서도 고려하며 이야기하는 것이 좋겠지!\n\n이런 팁을 참고해보면 여자친구와의 소통이 더 원할해질 거야. 행복한 관계를 위해 자유롭게 감정을 표현해보는 것이 중요하다는 걸 잊지마세요!"
      },
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 78,
    "completion_tokens": 452,
    "total_tokens": 530
  },
  "system_fingerprint": "fp_3b956da36b"
}
 */

/*
{
   "id": "chatcmpl-9LwiEKFItqJAwJtNHOwX01X1vhhrR",
   "object": "chat.completion",
   "created": 1715017606,
   "model": "gpt-3.5-turbo-0125",
   "choices": [
     {
       "index": 0,
       "message": {
         "role": "assistant",
         "content": "안녕! ESTP 유형인 당신에게 조언을 드릴게. 감정을 표현하는 것이 쉽지 않을 수 있지만, 이를 효과적으로 전달하기 위해서 몇 가지 팁을 준비했어. \n\n1. 직접적이고 솔직하게 표현하기: 당신은 직설적이고 현실적인 성향이 강할 거라고 생각해. 그러니 감정을 담담하고 직접적으로 표현할 필요가 있어.\n\n2. 구체적인 예시와 사례 제시하기: 감정을 설명할 때 구체적인 상황이나 사례를 들어 예시를 들어주면 상대방이 이해하기 쉬울 거야.\n\n3. 진심을 담아 표현하기: 진심이 담긴 어조와 몸짓으로 감정을 전달하면 상대방이 더 잘 이해할 수 있을 거야.\n\n4. 상대방의 시선에 서서 생각해보기: 상대방이 받아들일 수 있는 방식으로 감정을 전달하는 것이 중요해. 그래서 상대방의 입장에서도 고려하며 이야기하는 것이 좋겠지!\n\n이런 팁을 참고해보면 여자친구와의 소통이 더 원할해질 거야. 행복한 관계를 위해 자유롭게 감정을 표현해보는 것이 중요하다는 걸 잊지마세요!"
       },
       "logprobs": null,
       "finish_reason": "stop"
     }
   ],
   "usage": {
     "prompt_tokens": 78,
     "completion_tokens": 452,
     "total_tokens": 530
   },
   "system_fingerprint": "fp_3b956da36b"
 }

 */