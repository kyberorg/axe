package pm.axe.utils.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Password encryptor and comparator.
 */
@Component
@RequiredArgsConstructor
public class PasswordUtils {
    private final AsymmetricCryptTool asymmetricCryptTool;
    private final EncryptionUtils encryptionUtils;

    /**
     * Encrypts password. Salt added automatically.
     *
     * @param plainPassword not empty string with plain texted password to encrypt.
     * @return encrypted password
     */
    public String encryptPassword(final String plainPassword) {
        return asymmetricCryptTool.encrypt(constructPassword(plainPassword));
    }

    /**
     * Compares passwords.
     *
     * @param encryptedPassword string with encrypted password (typically taken from database)
     * @param passwordCandidate plain-text password to compare with.
     * @return true - if encrypted password string equals with encrypted (same way) password candidate string.
     */
    public boolean passwordMatches(final String encryptedPassword, final String passwordCandidate) {
        return asymmetricCryptTool.compare(encryptedPassword, constructPassword(passwordCandidate));
    }

    private String constructPassword(final String plainPassword) {
        final String serverSalt = encryptionUtils.getPasswordSalt();
        return plainPassword + serverSalt;
    }
}
