package gmr.aichat.backend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${MAIL_FROM:no-reply@hotmart-ai.local}")
            String from
    ) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendVerificationCode(
            String email,
            String code
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(email);
        message.setSubject(
                "Your verification code"
        );

        message.setText(
                """
                Your verification code is: %s

                This code expires in 5 minutes.

                If you did not request this code, ignore this email.
                """.formatted(code)
        );

        mailSender.send(message);
    }
}