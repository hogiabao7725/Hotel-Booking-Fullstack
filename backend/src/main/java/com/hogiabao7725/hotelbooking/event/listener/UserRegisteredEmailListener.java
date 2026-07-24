package com.hogiabao7725.hotelbooking.event.listener;

import com.hogiabao7725.hotelbooking.event.UserRegisteredEvent;
import com.hogiabao7725.hotelbooking.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEmailListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserRegisteredEvent event) {
        try {
            emailService.sendVerification(
                    event.email(),
                    event.fullName(),
                    event.token()
            );
        } catch (Exception ex) {
            log.error("Failed to send verification email to: {}", event.email(), ex);
        }
    }
}
