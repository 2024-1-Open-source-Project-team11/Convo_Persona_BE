package OSS_group11.ConvoPersona.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "feedback")
public class Feedback extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "feedback_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;     // 피드백 내용

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message gptMessage; //회원이 남긴 피드백이 어떤 gpt답변에 대한 피드백인지.

}
