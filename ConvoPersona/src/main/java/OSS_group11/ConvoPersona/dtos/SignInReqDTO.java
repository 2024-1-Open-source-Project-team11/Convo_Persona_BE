package OSS_group11.ConvoPersona.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignInReqDTO {

    private String name;
    private String password;

    public SignInReqDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
