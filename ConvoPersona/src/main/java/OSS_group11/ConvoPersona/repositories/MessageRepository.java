package OSS_group11.ConvoPersona.repositories;

import OSS_group11.ConvoPersona.domain.Message;
import OSS_group11.ConvoPersona.domain.Sender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrderByIdAsc(Sender sender);
}
