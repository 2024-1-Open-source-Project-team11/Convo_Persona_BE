package OSS_group11.ConvoPersona.repositories;

import OSS_group11.ConvoPersona.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Optional<Feedback> findByMemberId(Long memberId);
}
