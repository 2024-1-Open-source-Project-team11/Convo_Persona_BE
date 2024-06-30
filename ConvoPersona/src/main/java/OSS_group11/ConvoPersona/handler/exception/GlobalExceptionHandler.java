package OSS_group11.ConvoPersona.handler.exception;

import OSS_group11.ConvoPersona.exceptions.UserException;
import OSS_group11.ConvoPersona.handler.response.ApiResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice       //Controller 메서드가 요청받은 작업을 완수하지 못하고 Exception이 발생되면 Handler가 탐지
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ApiResponseHandler<Void> handleUserException(UserException e) {
        log.info("UserException: {}", e.getMessage());
        return ApiResponseHandler.fail(e.getResponseCode(), null);
    }
}
