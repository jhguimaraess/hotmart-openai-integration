package gmr.aichat.backend.common;

import java.time.Instant;

public record ApiError(
        int status,
        String message,
        Instant timestamp
) {
}