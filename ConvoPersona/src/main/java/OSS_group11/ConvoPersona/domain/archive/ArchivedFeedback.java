package OSS_group11.ConvoPersona.domain.archive;


import OSS_group11.ConvoPersona.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "archived_feedback")
public class ArchivedFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archived_feedback_id")
    private Long id;

    @Column(name = "archived_feedback_content")
    private String content; // 피드백 내용

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "archived_message_id")
    private ArchivedMessage archivedMessage;
}
