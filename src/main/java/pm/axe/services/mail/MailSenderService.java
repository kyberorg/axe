package pm.axe.services.mail;

import com.sun.mail.smtp.SMTPMessage;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pm.axe.mail.LetterType;
import pm.axe.utils.AppUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service, that prepares email letter and sends it via {@link JavaMailSender}.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MailSenderService {
    private static final String TAG = MailSenderService.class.getSimpleName();
    private static final String CORE_TEMPLATE = "mail.ftlh";
    private final JavaMailSender mailSender;
    private final AppUtils appUtils;

    private final Configuration configuration;

    /**
     * Prepares email letter.
     *
     * @param letterType  type of letter, linked to template (see {@link LetterType}).
     * @param targetEmail string with email address we should send to
     * @param subject     email subject
     * @param vars        template variables.
     * @return prepared email letter.
     * @throws MessagingException when message cannot be prepared correctly.
     * @throws IOException        when error occurs while reading or writing.
     * @throws TemplateException  when templating fails
     */
    public MimeMessage createLetter(final LetterType letterType, final String targetEmail, final String subject,
                                    final Map<String, Object> vars)
            throws MessagingException, IOException, TemplateException {
        final MimeMessage mailMessage = getEmptyMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true, StandardCharsets.UTF_8.toString());

        String appName = appUtils.getApplicationName();
        String fullSubject = StringUtils.isNotBlank(appName) ? appName + " " + subject : subject;
        vars.put("subject", fullSubject);

        String letterHtmlBody = createLetterBody(letterType, vars);

        helper.setReplyTo(appUtils.getEmailFromAddress());
        helper.setFrom(appUtils.getEmailFromAddress(), "Aleks from " + appUtils.getApplicationName());
        helper.setTo(targetEmail);
        helper.setSubject(fullSubject);

        helper.setText(letterHtmlBody, true);
        helper.addInline("axeLogo", new ClassPathResource("email_logo.png"));

        SMTPMessage smtpMessage = new SMTPMessage(mailMessage);
        smtpMessage.setEnvelopeFrom(appUtils.getEmailFromAddress());
        return smtpMessage;
    }

    /**
     * Sends letter.
     *
     * @param emailAddress string with email address we send to.
     * @param letter       prepared by {@link #createLetter(LetterType, String, String, Map)} letter.
     */
    @Async
    public void sendEmail(final String emailAddress, final MimeMessage letter) {
        Object subject;
        try {
            subject = letter.getSubject();
        } catch (MessagingException e) {
            subject = letter;
        }
        log.info("{} Sending \"{}\" to {}", TAG, subject, emailAddress);
        mailSender.send(letter);
    }

    private MimeMessage getEmptyMimeMessage() {
        return mailSender.createMimeMessage();
    }

    private String createLetterBody(final LetterType letterType, final Map<String, Object> vars)
            throws TemplateException, IOException {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("siteUrl", appUtils.getServerUrl());
        model.put("templateFile", letterType.getTemplateFile());
        if (vars != null && !vars.isEmpty()) {
            model.putAll(vars);
        }

        configuration.getTemplate(CORE_TEMPLATE).process(model, stringWriter);

        return stringWriter.getBuffer().toString();
    }
}
