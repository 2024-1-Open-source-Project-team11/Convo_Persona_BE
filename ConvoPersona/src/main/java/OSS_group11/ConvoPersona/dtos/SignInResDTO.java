package OSS_group11.ConvoPersona.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignInResDTO {
    private String id;

    public SignInResDTO(String id) {
        this.id = id;
    }
}
