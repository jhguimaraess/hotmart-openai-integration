package gmr.aichat.backend.auth.dto;

public record AuthenticatedUserResponse(
        String id,
        String email
) {
}
