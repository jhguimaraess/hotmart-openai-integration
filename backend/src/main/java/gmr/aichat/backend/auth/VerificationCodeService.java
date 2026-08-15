package gmr.aichat.backend.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

@Service
public class VerificationCodeService {

    private static final String CODE_KEY_PREFIX =
            "auth:verification:";

    private static final String COOLDOWN_KEY_PREFIX =
            "auth:verification:cooldown:";

    private static final Duration CODE_TTL =
            Duration.ofMinutes(5);

    private static final Duration COOLDOWN_TTL =
            Duration.ofSeconds(60);

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public VerificationCodeService(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreCode(String email) {

        String normalizedEmail = normalizeEmail(email);

        String codeKey =
                CODE_KEY_PREFIX + normalizedEmail;

        String cooldownKey =
                COOLDOWN_KEY_PREFIX + normalizedEmail;

        Boolean cooldownCreated =
                redisTemplate
                        .opsForValue()
                        .setIfAbsent(
                                cooldownKey,
                                "1",
                                COOLDOWN_TTL
                        );

        if (!Boolean.TRUE.equals(cooldownCreated)) {
            throw new IllegalStateException(
                    "Verification code requested too recently"
            );
        }

        String code = generateCode();

        redisTemplate
                .opsForValue()
                .set(
                        codeKey,
                        code,
                        CODE_TTL
                );

        return code;
    }

    public boolean validateAndConsumeCode(
            String email,
            String code
    ) {

        String normalizedEmail = normalizeEmail(email);

        String codeKey =
                CODE_KEY_PREFIX + normalizedEmail;

        Boolean deleted =
                redisTemplate.compareAndDelete(
                        codeKey,
                        code
                );

        return Boolean.TRUE.equals(deleted);
    }

    private String generateCode() {

        int number =
                secureRandom.nextInt(1_000_000);

        return "%06d".formatted(number);
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}