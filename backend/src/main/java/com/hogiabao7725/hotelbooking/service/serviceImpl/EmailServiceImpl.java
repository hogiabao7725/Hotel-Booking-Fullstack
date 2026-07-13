package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.FrontendProperties;
import com.hogiabao7725.hotelbooking.config.properties.MailProperties;
import com.hogiabao7725.hotelbooking.exception.BusinessException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final FrontendProperties frontendProperties;
    private final MailProperties mailProperties;

    @Override
    public void sendVerification(String to, String username, String token) {
        try {
            Context context = new Context();
            context.setVariable("username", username);

            String verificationLink = String.format("%s/auth/verify-email?token=%s",
                    frontendProperties.url(), token);
            context.setVariable("verificationLink", verificationLink);

            String htmlContent = templateEngine.process("emails/email-verification", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(mailProperties.from());
            helper.setTo(to);
            helper.setSubject("Hotel Booking - Account Verification");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new BusinessException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }
}
