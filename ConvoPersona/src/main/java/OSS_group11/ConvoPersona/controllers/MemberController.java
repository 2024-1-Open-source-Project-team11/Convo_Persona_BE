package OSS_group11.ConvoPersona.controllers;

import OSS_group11.ConvoPersona.dtos.SignInReqDTO;
import OSS_group11.ConvoPersona.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /***
     * Member테이블 조회, (name, password) 존재하면 해당 userId 반환
     * @return
     */
    @PostMapping("/user")
    public ResponseEntity<String> signIn(@RequestBody SignInReqDTO signInReqDTO) {
        Long memberId = memberService.login(signInReqDTO.getName(), signInReqDTO.getPassword());
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 잘못된 사용자 이름 또는 비밀번호입니다.");
        }
        return ResponseEntity.ok(memberId.toString());
    }
}
