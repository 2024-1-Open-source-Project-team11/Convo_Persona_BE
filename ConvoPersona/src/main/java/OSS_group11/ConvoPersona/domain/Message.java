package OSS_group11.ConvoPersona.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Message extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Sender sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @JsonManagedReference
    @OneToOne(mappedBy = "gptMessage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Feedback feedback;

    @Builder
    public Message(Long id, Sender sender, String content, Chat chat, Feedback feedback) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.chat = chat;
        this.feedback = feedback;
    }
}
