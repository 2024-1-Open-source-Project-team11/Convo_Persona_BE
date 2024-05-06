package OSS_group11.ConvoPersona.repositories;

import OSS_group11.ConvoPersona.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByNameAndPassword(String name, String password);
}
