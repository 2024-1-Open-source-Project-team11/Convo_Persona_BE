package OSS_group11.ConvoPersona.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignUpResDTO {
    String id;

    public SignUpResDTO(String id) {
        this.id = id;
    }
}
