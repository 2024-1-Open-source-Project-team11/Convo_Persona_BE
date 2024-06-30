package OSS_group11.ConvoPersona.controllers;

import OSS_group11.ConvoPersona.dtos.SignInReqDTO;
import OSS_group11.ConvoPersona.dtos.SignInResDTO;
import OSS_group11.ConvoPersona.dtos.SignUpReqDTO;
import OSS_group11.ConvoPersona.dtos.SignUpResDTO;
import OSS_group11.ConvoPersona.handler.response.ApiResponseHandler;
import OSS_group11.ConvoPersona.handler.response.ResponseCode;
import OSS_group11.ConvoPersona.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/user/sign-up")
    @CrossOrigin(origins = "https://convo-persona.netlify.app")
    public ApiResponseHandler<SignUpResDTO> signUp(@RequestBody SignUpReqDTO signUpReqDTO) {
//        SignUpResDTO signUpResDTO = memberService.join(signUpReqDTO);
        return ApiResponseHandler.success(ResponseCode.USER_CREATE_SUCCESS, memberService.join(signUpReqDTO));
//        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResDTO);
    }

    /***
     * Member테이블 조회, (name, password) 존재하면 해당 userId 반환
     * @return
     */
    @PostMapping("/user/sign-in")
    @CrossOrigin(origins = "https://convo-persona.netlify.app")
//    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> signIn(@RequestBody SignInReqDTO signInReqDTO) {
        Long memberId = memberService.login(signInReqDTO.getName(), signInReqDTO.getPassword());

        //회원이 DB에 없으면, memberId에 에러메시지 문자열 걍 넣어버림
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: 잘못된 사용자 이름 또는 비밀번호입니다.");
        }
        SignInResDTO signInResDTO = new SignInResDTO(memberId.toString());
        return ResponseEntity.ok(signInResDTO);
    }
}
