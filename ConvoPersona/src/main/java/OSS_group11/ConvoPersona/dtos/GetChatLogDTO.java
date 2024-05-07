package OSS_group11.ConvoPersona.dtos;

import OSS_group11.ConvoPersona.domain.Message;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GetChatLogDTO {
    @JsonProperty(value = "id")
    private Long chatId;
    private List<Message> message;

    public GetChatLogDTO(Long chatId, List<Message> message) {
        this.chatId = chatId;
        this.message = message;
    }
}
