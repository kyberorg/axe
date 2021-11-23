package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.constants.App;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Component
public class EncryptionUtils {
    private static final String NO_SERVER_KEY = "NO_SERVER_KEY";

    private final String passwordSalt;
    private final String serverKey;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final TextEncryptor symmetricEncryptor;

    private final Environment env;

    public EncryptionUtils(final Environment env) {
        this.env = env;
        this.serverKey = setServerKey();
        this.passwordSalt = setPasswordSalt();
        this.symmetricEncryptor = Encryptors.text(getServerKey(), getPasswordSalt());
    }

    private String setServerKey() {
        String serverKey = env.getProperty(App.Properties.SERVER_KEY, NO_SERVER_KEY);
        if (serverKey.equals(NO_SERVER_KEY)) {
            log.debug("No Server Key defined - using empty string instead");
            return "";
        } else {
            return serverKey;
        }
    }

    private String setPasswordSalt() {
        return KeyGenerators.string().generateKey();
    }
}
