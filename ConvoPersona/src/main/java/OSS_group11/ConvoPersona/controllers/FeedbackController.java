package OSS_group11.ConvoPersona.controllers;

import OSS_group11.ConvoPersona.dtos.AddFeedbackReqDTO;
import OSS_group11.ConvoPersona.services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /***
     * message_id랑, feedback 내용이 requestBody에 담겨 요청이 올 것이다.
     * @param addFeedbackReqDTO
     * @return
     */
    @PostMapping("/feedback")
    @CrossOrigin(origins = "https://convo-persona.netlify.app")
    public ResponseEntity<?> createFeedback(@RequestHeader("Authorization") Long memberId,
                                            @RequestBody AddFeedbackReqDTO addFeedbackReqDTO) {
        feedbackService.saveFeedback(memberId, addFeedbackReqDTO);
        return ResponseEntity.ok("feedback 저장 완료");
    }
}
