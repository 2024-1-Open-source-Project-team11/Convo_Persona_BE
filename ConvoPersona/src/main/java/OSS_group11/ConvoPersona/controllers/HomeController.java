package OSS_group11.ConvoPersona.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "홈페이지 메인 화면";
    }

    @GetMapping("/home")
    public String home() {
//        System.out.println("controller 진입 성공");
        return "GET /home 요청";
    }

}
