package OSS_group11.ConvoPersona.domain.archive;

import OSS_group11.ConvoPersona.domain.Mbti;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "archived_message")
public class ArchivedMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archived_message_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "archived_chat_id")
    private ArchivedChat archivedChat;

    @Column(name = "archived_message_content")
    private String content;

    @Column(name = "archived_message_mbti")
    @Enumerated(EnumType.STRING)
    private Mbti mbti;

    @Column(name = "archived_message_created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "archivedMessage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ArchivedFeedback feedback;
}
