package libreria.libreria.exception;

import lombok.*;

@Data
public class RestErrorResult {

    private String code;
    private String message;

    @Builder
    public RestErrorResult(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
