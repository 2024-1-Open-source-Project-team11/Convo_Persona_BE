package OSS_group11.ConvoPersona.repositories;

import OSS_group11.ConvoPersona.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByNameAndPassword(String name, String password);

    Optional<Member> findById(Long memberId);
}
