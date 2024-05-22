package OSS_group11.ConvoPersona.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AddFeedbackReqDTO {

    @JsonProperty("id")
    private Long messageId;        //피드백을 남긴 메시지 id

    @JsonProperty("feedback")
    private String feedbackContent; //피드백 내용
}
