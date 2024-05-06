package OSS_group11.ConvoPersona.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MbtiPredictionOutputDTO {

    private String mbti;

    public MbtiPredictionOutputDTO(String mbti) {
        this.mbti = mbti;
    }
}
