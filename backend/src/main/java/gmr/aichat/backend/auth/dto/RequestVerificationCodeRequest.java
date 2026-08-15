package gmr.aichat.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestVerificationCodeRequest(

        @NotBlank
        @Email
        String email

) {
}