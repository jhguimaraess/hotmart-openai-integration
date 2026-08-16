package gmr.aichat.backend.auth.exception;

public class VerificationCodeCooldownException extends RuntimeException {

    public VerificationCodeCooldownException() {
        super("Verification code requested too recently");
    }
}