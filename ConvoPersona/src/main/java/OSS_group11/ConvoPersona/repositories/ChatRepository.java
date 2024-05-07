package OSS_group11.ConvoPersona.repositories;

import OSS_group11.ConvoPersona.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    public Optional<Chat> findByMemberId(Long memberId);
}
