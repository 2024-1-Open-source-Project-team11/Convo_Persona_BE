package OSS_group11.ConvoPersona.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Message extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private int id;

    @Enumerated(EnumType.STRING)
    private Sender sender;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    @Nullable
    private Chat chat;

    @OneToOne(mappedBy = "gptMessage", fetch = FetchType.LAZY)
    private Feedback feedback;

    public Message(int id, Sender sender, String content) {
        this.id = id;
        this.sender = sender;
        this.content = content;
    }
}
