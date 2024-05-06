package OSS_group11.ConvoPersona.dtos;

import OSS_group11.ConvoPersona.domain.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetChatLogDTO {
    private Long chatId;
    private List<Message> messageList;

    public GetChatLogDTO(Long chatId, List<Message> messageList) {
        this.chatId = chatId;
        this.messageList = messageList;
    }
}
