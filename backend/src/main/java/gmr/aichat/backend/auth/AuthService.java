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

        verificationCodeService.generateAndStoreCode(
                user.getEmail()
        );

        logger.info(
                "Verification code generated for user {}",
                user.getEmail()
        );
    }
}