package pm.axe.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import pm.axe.Axe;

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
        } else if (mailUser.equals(Axe.C.MINUS) || mailPassword.equals(Axe.C.MINUS)) {
            return noOpSender();
        } else {
            return axeSender();
        }
    }

    private JavaMailSender axeSender() {
        JavaMailSenderImpl axeSender = new JavaMailSenderImpl();
        axeSender.setHost("smtp.kmail.ee");
        axeSender.setPort(SMTP_TLS_PORT);

        axeSender.setUsername(mailUser);
        axeSender.setPassword(mailPassword);

        Properties props = axeSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", mailDebug);

        return axeSender;
    }

    private JavaMailSender noOpSender() {
        return new JavaMailSenderImpl();
    }
}
