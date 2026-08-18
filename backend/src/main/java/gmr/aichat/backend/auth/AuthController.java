package gmr.aichat.backend.auth;

import gmr.aichat.backend.auth.dto.AuthenticatedUserResponse;
import gmr.aichat.backend.auth.dto.RequestVerificationCodeRequest;
import gmr.aichat.backend.auth.dto.TokenResponse;
import gmr.aichat.backend.auth.dto.VerifyCodeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/code")
    public ResponseEntity<Void> requestVerificationCode(
            @RequestBody @Valid RequestVerificationCodeRequest request
    ) {

        authService.requestVerificationCode(
                request.email()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<TokenResponse> verifyCode(
            @RequestBody
            @Valid
            VerifyCodeRequest request
    ) {

        TokenResponse response =
                authService.verifyCode(
                        request.email(),
                        request.code()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticatedUserResponse> me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        AuthenticatedUserResponse response =
                new AuthenticatedUserResponse(
                        jwt.getSubject(),
                        jwt.getClaimAsString("email")
                );

        return ResponseEntity.ok(response);
    }
}