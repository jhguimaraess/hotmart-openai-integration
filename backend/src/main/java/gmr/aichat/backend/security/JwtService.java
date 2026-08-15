package gmr.aichat.backend.security;

import gmr.aichat.backend.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expirationSeconds;

    public JwtService(
            JwtEncoder jwtEncoder,

            @Value("${jwt.expiration-seconds:1800}")
            long expirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(User user) {

        Instant now = Instant.now();

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer("hotmart-ai-backend")
                        .subject(user.getId().toString())
                        .issuedAt(now)
                        .expiresAt(
                                now.plusSeconds(expirationSeconds)
                        )
                        .claim(
                                "email",
                                user.getEmail()
                        )
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(claims)
                )
                .getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}