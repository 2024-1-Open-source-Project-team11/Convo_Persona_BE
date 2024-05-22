package OSS_group11.ConvoPersona.repositories.archive;

import OSS_group11.ConvoPersona.domain.archive.ArchivedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivedMessageRepository extends JpaRepository<ArchivedMessage, Long> {
}
