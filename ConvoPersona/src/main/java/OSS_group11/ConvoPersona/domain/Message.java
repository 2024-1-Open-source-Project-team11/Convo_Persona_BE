package OSS_group11.ConvoPersona.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Enumerated(EnumType.STRING)
    private Mbti mbti;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @JsonManagedReference
    @OneToOne(mappedBy = "gptMessage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Feedback feedback;

    public void updateMbti(String mbti) {
        try {
            this.mbti = Mbti.valueOf(mbti);
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않은 MBTI 유형: " + mbti);
        }
    }
}
