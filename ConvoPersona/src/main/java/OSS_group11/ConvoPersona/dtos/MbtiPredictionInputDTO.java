package OSS_group11.ConvoPersona.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MbtiPredictionInputDTO {

    @JsonProperty("user_prompt_list")
    private List<String> userPromptList;

    public MbtiPredictionInputDTO(List<String> userPromptList) {
        this.userPromptList = userPromptList;
    }
}
