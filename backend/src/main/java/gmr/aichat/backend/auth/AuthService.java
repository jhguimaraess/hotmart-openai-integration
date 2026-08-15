package gmr.aichat.backend.auth;

import gmr.aichat.backend.auth.dto.TokenResponse;
import gmr.aichat.backend.auth.exception.InvalidVerificationCodeException;
import gmr.aichat.backend.security.JwtService;
import gmr.aichat.backend.user.User;
import gmr.aichat.backend.user.UserService;
import gmr.aichat.backend.user.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;
    private final JwtService jwtService;

    public AuthService(
            UserService userService,
            VerificationCodeService verificationCodeService,
            EmailService emailService, JwtService jwtService
    ) {
        this.userService = userService;
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }

    public void requestVerificationCode(String email) {

        var userOptional =
                userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            logger.info(
                    "Verification code request ignored for ineligible user"
            );

            return;
        }

        User user = userOptional.get();

        if (user.getStatus() != UserStatus.ACTIVE) {
            logger.info(
                    "Verification code request ignored for ineligible user"
            );

            return;
        }

        String code =
                verificationCodeService.generateAndStoreCode(
                        user.getEmail()
                );

        emailService.sendVerificationCode(
                user.getEmail(),
                code
        );

        logger.info(
                "Verification code generated for user {}",
                user.getEmail()
        );
    }

    public TokenResponse verifyCode(
            String email,
            String code
    ) {

        User user = userService
                .findByEmail(email)
                .filter(foundUser ->
                        foundUser.getStatus()
                                == UserStatus.ACTIVE
                )
                .orElseThrow(
                        InvalidVerificationCodeException::new
                );

        boolean valid =
                verificationCodeService
                        .validateAndConsumeCode(
                                user.getEmail(),
                                code
                        );

        if (!valid) {
            throw new InvalidVerificationCodeException();
        }

        String token =
                jwtService.generateToken(user);

        logger.info(
                "User {} authenticated successfully",
                user.getEmail()
        );

        return new TokenResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds()
        );
    }
}