package gmr.aichat.backend.auth;

import gmr.aichat.backend.auth.exception.VerificationCodeCooldownException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;

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
    private final String verificationCodeSecret;

    public VerificationCodeService(
            StringRedisTemplate redisTemplate,

            @Value("${auth.verification-code-secret}")
            String verificationCodeSecret
    ) {
        this.redisTemplate = redisTemplate;
        this.verificationCodeSecret =
                verificationCodeSecret;
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
            throw new VerificationCodeCooldownException();
        }

        String code = generateCode();

        String codeHash =
                hashCode(normalizedEmail, code);

        redisTemplate
                .opsForValue()
                .set(
                        codeKey,
                        codeHash,
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

        String codeHash =
                hashCode(normalizedEmail, code);

        Boolean deleted =
                redisTemplate.compareAndDelete(
                        codeKey,
                        codeHash
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

    private String hashCode(
            String normalizedEmail,
            String code
    ) {

        try {

            Mac mac =
                    Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            verificationCodeSecret.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            "HmacSHA256"
                    );

            mac.init(secretKey);

            String value =
                    normalizedEmail + ":" + code;

            byte[] hash =
                    mac.doFinal(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(hash);

        } catch (GeneralSecurityException exception) {

            throw new IllegalStateException(
                    "Could not hash verification code",
                    exception
            );
        }
    }
}