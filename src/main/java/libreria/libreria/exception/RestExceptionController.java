package libreria.libreria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class RestExceptionController {

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public RestErrorResult handleMultipartExceptionPayloadTooLarge(Exception e) {
        e.printStackTrace();
        return RestErrorResult.builder()
                .code("413")
                .message(e.getMessage())
                .build();
    }
}
