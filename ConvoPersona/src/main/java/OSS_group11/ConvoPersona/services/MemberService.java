package OSS_group11.ConvoPersona.services;


import OSS_group11.ConvoPersona.domain.Member;
import OSS_group11.ConvoPersona.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /***
     * MemberRepository에서 name, password로 회원 조회 -> 회원 존재하면 id 반환
     * @param name
     * @param password
     * @return
     */
    public Long login(String name, String password) {
        Member member = memberRepository.findByNameAndPassword(name, password);
        if (member != null) {
            return member.getId(); // Return the member's id if found
        } else {
            return null; // Return null if no member found
        }
    }
}
