package OSS_group11.ConvoPersona.dtos;

import OSS_group11.ConvoPersona.domain.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddChatResDTO {
    private Message userMessage;
    private Message gptMessage;

    public AddChatResDTO(Message userMessage, Message gptMessage) {
        this.userMessage = userMessage;
        this.gptMessage = gptMessage;
    }
}
