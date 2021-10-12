package io.kyberorg.yalsee.services.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class EmailSenderService {
    private final static String TAG = EmailSenderService.class.getSimpleName();

    @Async
    public void sendEmail(String emailAddress) {
        //TODO implement
        log.info("{} Sending email to {}", TAG, emailAddress);
    }
}
