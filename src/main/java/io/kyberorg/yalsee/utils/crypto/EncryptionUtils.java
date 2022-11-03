package io.kyberorg.yalsee.utils.crypto;

import io.kyberorg.yalsee.constants.App;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Slf4j
@Data
@Component
class EncryptionUtils {
    private static final String NO_SERVER_KEY = "NO_SERVER_KEY";
    private static final String NO_PASSWORD_SALT = "NO_PASSWORD_SALT";

    private final String passwordSalt;
    private final String serverKey;
    private final PasswordEncoder asymmetricEncoder = new BCryptPasswordEncoder();
    private final TextEncryptor symmetricEncryptor;
    private final TextEncryptor easySymmetricEncryptor;

    private final Environment env;

    public EncryptionUtils(final Environment env) {
        this.env = env;
        this.serverKey = setServerKey();
        this.passwordSalt = setPasswordSalt();
        this.symmetricEncryptor = Encryptors.delux(getServerKey(), getPasswordSalt());
        this.easySymmetricEncryptor = Encryptors.text(getServerKey(), getPasswordSalt());
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
        String envPasswordSalt = env.getProperty(App.Properties.PASSWORD_SALT, NO_PASSWORD_SALT);
        final String passwordSalt;
        if (envPasswordSalt.equals(NO_PASSWORD_SALT)) {
            passwordSalt = getServerKey();
        } else {
            passwordSalt = envPasswordSalt;
        }
        return Hex.encodeHexString(passwordSalt.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8));
    }
}
