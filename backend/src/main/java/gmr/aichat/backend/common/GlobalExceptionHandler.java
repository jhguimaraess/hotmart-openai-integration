package gmr.aichat.backend.common;

import gmr.aichat.backend.auth.exception.VerificationCodeCooldownException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VerificationCodeCooldownException.class)
    public ResponseEntity<ApiError> handleVerificationCodeCooldown(
            VerificationCodeCooldownException exception
    ) {

        ApiError error = new ApiError(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                exception.getMessage(),
                Instant.now()
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(error);
    }
}