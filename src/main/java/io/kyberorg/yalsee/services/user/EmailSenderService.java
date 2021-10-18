package io.kyberorg.yalsee.services.user;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class EmailSenderService {
    private static final String TAG = EmailSenderService.class.getSimpleName();
    private static final String CORE_TEMPLATE = "mail.ftlh";
    private final JavaMailSender mailSender;
    private final AppUtils appUtils;

    private final Configuration configuration;

    @Async
    public void sendEmail(final String emailAddress, final MimeMessage letter) {
        log.info("{} Sending {} to {}", TAG, letter, emailAddress);
        mailSender.send(letter);
    }

    public MimeMessage createLetter(final Letter letterType, final String email, final String subject,
                                    final Map<String, Object> vars)
            throws MessagingException, IOException, TemplateException {
        final MimeMessage mailMessage = getEmptyMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, StandardCharsets.UTF_8.toString());

        String letterHtmlBody = createLetterBody(letterType, vars);

        helper.setReplyTo(appUtils.getFromAddress());
        helper.setFrom(appUtils.getFromAddress());
        helper.setTo(email);

        String instancePrefix = appUtils.getInstancePrefixForMail();
        if (StringUtils.isNotBlank(instancePrefix)) {
            helper.setSubject(instancePrefix + " " + subject);
        } else {
            helper.setSubject(subject);
        }
        helper.setText(letterHtmlBody, true);
        return mailMessage;
    }

    private MimeMessage getEmptyMimeMessage() {
        return mailSender.createMimeMessage();
    }

    private String createLetterBody(Letter letterType, Map<String, Object> vars) throws TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("template", letterType.name().toLowerCase(Locale.ROOT));
        if (vars != null && !vars.isEmpty()) {
            model.putAll(vars);
        }

        configuration.getTemplate(CORE_TEMPLATE).process(model, stringWriter);

        return stringWriter.getBuffer().toString();
    }

    /**
     * Letter template.
     * {@link #name().toLowerCase()} should correspond with strings in mail.ftlh
     */
    public enum Letter {
        ACCOUNT_CONFIRMATION,
        OTP
    }
}
