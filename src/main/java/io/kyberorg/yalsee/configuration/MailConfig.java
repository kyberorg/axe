package io.kyberorg.yalsee.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Mail Configuration. Credentials and server.
 */
@Configuration
public class MailConfig {
    private static final int SMTP_TLS_PORT = 587;

    @Value("${app.mail.credentials.username}")
    private String mailUser;

    @Value("${app.mail.credentials.password}")
    private String mailPassword;

    @Value("${app.mail.debug}")
    private boolean mailDebug;

    /**
     * Provides Bean that actually performs sending.
     *
     * @return if both {@link #mailUser} and {@link #mailPassword} are set - configured SMTP Sender,
     * if not - NoOp Sender (one that does nothing).
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        if (StringUtils.isAnyBlank(mailUser, mailPassword)) {
            return noOpSender();
        } else {
            return gmailSender();
        }
    }

    private JavaMailSender gmailSender() {
        JavaMailSenderImpl gMailSender = new JavaMailSenderImpl();
        gMailSender.setHost("smtp.gmail.com");
        gMailSender.setPort(SMTP_TLS_PORT);

        gMailSender.setUsername(mailUser);
        gMailSender.setPassword(mailPassword);

        Properties props = gMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        //props.put("mail.smtp.from", "Yalsee Dev <dev@yals.ee>"); //FIXME remove hardcode
        props.put("mail.debug", mailDebug);

        return gMailSender;
    }

    private JavaMailSender noOpSender() {
        return new JavaMailSenderImpl();
    }
}
