package OSS_group11.ConvoPersona.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "feedback")
public class Feedback extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "feedback_id")
    private Long id;

    private String content;     // 피드백 내용

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message gptMessage; //회원이 남긴 피드백이 어떤 gpt답변에 대한 피드백인지.

}
