package OSS_group11.ConvoPersona.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private int id;
    private Sender sender;
    private String content;

    public Message(int id, Sender sender, String content) {
        this.id = id;
        this.sender = sender;
        this.content = content;
    }
}
