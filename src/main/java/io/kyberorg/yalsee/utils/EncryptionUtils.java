package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.constants.App;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
public class EncryptionUtils {
    private static final String NO_PASSWORD_SALT = "NO_PASSWORD_SALT";
    private static final String NO_SERVER_KEY = "NO_SERVER_KEY";

    private String passwordSalt;
    private String serverKey;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Environment env;

    public EncryptionUtils(final Environment env) {
        this.env = env;
        populateStaticFields();
    }

    private void populateStaticFields() {
        passwordSalt = setPasswordSalt();
        serverKey = setServerKey();
    }

    public String setPasswordSalt() {
        String passwordSalt = env.getProperty(App.Properties.PASSWORD_SALT, NO_PASSWORD_SALT);
        if (passwordSalt.equals(NO_PASSWORD_SALT)) {
            log.debug("No Password Salt defined - using empty String (with weak security)");
            return "";
        } else {
            return passwordSalt;
        }
    }

    public String setServerKey() {
        String serverKey = env.getProperty(App.Properties.SERVER_KEY, NO_SERVER_KEY);
        if (serverKey.equals(NO_SERVER_KEY)) {
            log.debug("No Server Key defined - using empty string instead");
            return "";
        } else {
            return serverKey;
        }
    }
}
