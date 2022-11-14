package io.kyberorg.yalsee.utils.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Crypto Tool that provides Asymmetric Encryption (one-way encryption).
 */
@Component
@RequiredArgsConstructor
public class AsymmetricCryptTool {
    private final EncryptionUtils encryptionUtils;

    /**
     * Encrypts given plain-text string.
     *
     * @param plainText string to encrypt.
     * @return encrypted string.
     */
    public String encrypt(final String plainText) {
        return encryptionUtils.getAsymmetricEncoder().encode(plainText);
    }

    /**
     * Encrypts plain-text string and compares it with its encrypted version.
     *
     * @param encryptedText string with encrypted text (typically taken from database)
     * @param plainText     string with unencrypted text to compare
     * @return true - if plain text string encrypted same way equals with encrypted text, false - if not.
     */
    public boolean compare(final String encryptedText, final String plainText) {
        return encryptionUtils.getAsymmetricEncoder().encode(plainText).equals(encryptedText);
    }
}
