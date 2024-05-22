package OSS_group11.ConvoPersona.repositories.archive;

import OSS_group11.ConvoPersona.domain.archive.ArchivedChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedChatRepository extends JpaRepository<ArchivedChat, Long> {
}
