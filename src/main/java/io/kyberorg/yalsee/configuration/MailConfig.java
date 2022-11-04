package io.kyberorg.yalsee.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${app.mail.credentials.username}")
    private String mailUser;

    @Value("${app.mail.credentials.password}")
    private String mailPassword;

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
        gMailSender.setPort(587);

        gMailSender.setUsername(mailUser);
        gMailSender.setPassword(mailPassword);

        Properties props = gMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return gMailSender;
    }

    private JavaMailSender noOpSender() {
        return new JavaMailSenderImpl();
    }
}