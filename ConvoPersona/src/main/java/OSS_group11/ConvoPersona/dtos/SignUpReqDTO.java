package OSS_group11.ConvoPersona.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpReqDTO {
    private String name;
    private String email;
    private String password;

    public SignUpReqDTO(String name, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
