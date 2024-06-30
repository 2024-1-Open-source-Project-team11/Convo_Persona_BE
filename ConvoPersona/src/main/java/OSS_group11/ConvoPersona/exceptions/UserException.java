package OSS_group11.ConvoPersona.exceptions;


import OSS_group11.ConvoPersona.handler.response.ResponseCode;

public class UserException extends BaseException {

    public UserException(ResponseCode responseCode) {
        super(responseCode);
    }
}
