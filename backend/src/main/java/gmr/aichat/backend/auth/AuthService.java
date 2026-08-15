package gmr.aichat.backend.auth;

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

    public AuthService(
            UserService userService,
            VerificationCodeService verificationCodeService
    ) {
        this.userService = userService;
        this.verificationCodeService = verificationCodeService;
    }

    public void requestVerificationCode(String email) {

        User user = userService
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "User is not active"
            );
        }

        verificationCodeService.generateAndStoreCode(
                user.getEmail()
        );

        logger.info(
                "Verification code generated for user {}",
                user.getEmail()
        );
    }
}