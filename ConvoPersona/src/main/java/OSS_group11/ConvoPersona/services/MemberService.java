package OSS_group11.ConvoPersona.services;


import OSS_group11.ConvoPersona.domain.Member;
import OSS_group11.ConvoPersona.dtos.SignUpReqDTO;
import OSS_group11.ConvoPersona.dtos.SignUpResDTO;
import OSS_group11.ConvoPersona.repositories.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
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

    /***
     * 회원가입 기능
     * @param signUpReqDTO
     */
    public SignUpResDTO join(SignUpReqDTO signUpReqDTO) {
        //이미 회원가입 한 회원인지 확인
        Optional<Member> byEmail = memberRepository.findByEmail(signUpReqDTO.getEmail());

        if (byEmail.isPresent()) {
            return null;        //return null if member found by email is already exist
        }

        Member newMember = Member.builder()
                .name(signUpReqDTO.getName())
                .email(signUpReqDTO.getEmail())
                .password(signUpReqDTO.getPassword())
                .build();

        Member savedMember = memberRepository.save(newMember);

        return new SignUpResDTO(savedMember.getId().toString());
    }
}
