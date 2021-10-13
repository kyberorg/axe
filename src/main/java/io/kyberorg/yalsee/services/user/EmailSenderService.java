package io.kyberorg.yalsee.services.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class EmailSenderService {
    private final static String TAG = EmailSenderService.class.getSimpleName();

    @Async
    public void sendEmail(final String emailAddress, final SimpleMailMessage letter) {
        //TODO implement
        log.info("{} Sending {} to {}", TAG, letter, emailAddress);
    }
}
