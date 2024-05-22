package OSS_group11.ConvoPersona.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class Chat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonManagedReference
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Message> messages;

    @Builder
    public Chat(Long chatId, Member member, List<Message> messages) {
        this.chatId = chatId;
        this.member = member;
        this.messages = messages;
    }
}
