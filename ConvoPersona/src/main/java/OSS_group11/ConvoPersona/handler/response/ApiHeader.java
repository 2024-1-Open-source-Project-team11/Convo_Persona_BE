package OSS_group11.ConvoPersona.handler.response;

import lombok.Getter;

@Getter
public class ApiHeader {

    private int code;
    private String message;

    public ApiHeader(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
