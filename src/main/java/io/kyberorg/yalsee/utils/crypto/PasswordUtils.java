package io.kyberorg.yalsee.utils.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtils {
    private final AsymmetricCryptTool asymmetricCryptTool;
    private final EncryptionUtils encryptionUtils;

    public String encryptPassword(final String plainPassword) {
        return asymmetricCryptTool.encrypt(constructPassword(plainPassword));
    }

    public boolean passwordMatches(final String encryptedPassword, final String passwordCandidate) {
        return asymmetricCryptTool.compare(encryptedPassword, constructPassword(passwordCandidate));
    }

    private String constructPassword(final String plainPassword) {
        final String serverSalt = encryptionUtils.getPasswordSalt();
        return plainPassword + serverSalt;
    }
}
