package OSS_group11.ConvoPersona.handler.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponseHandler<T> {

    private final int code;         //response status code
    private final String message;   //response explanation
    private final T data;           //response body data


    public static <T> ApiResponseHandler<T> success(ResponseCode code, T data) {
        return new ApiResponseHandler<T>(code.getHttpStatusCode(), code.getMessage(), data);
    }

    public static <T> ApiResponseHandler<T> fail(ResponseCode code, T data) {
        return new ApiResponseHandler<T>(code.getHttpStatusCode(), code.getMessage(), data);
    }
}
