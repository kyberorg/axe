package io.kyberorg.yalsee.utils.crypto;

import io.kyberorg.yalsee.result.OperationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Symmetric encryption tool. Two-way encryption (encryption/decryption).
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SymmetricCryptTool {
    private static final String TAG = "[" + SymmetricCryptTool.class.getSimpleName() + "]";

    private final EncryptionUtils encryptionUtils;

    /**
     * Encrypt text with symmetric algorithm.
     *
     * @param plainText string to encrypt
     * @return {@link OperationResult} with encrypted in payload or with error.
     */
    public OperationResult encrypt(final String plainText) {
        try {
            String encryptedString = encryptionUtils.getSymmetricEncryptor().encrypt(plainText);
            return OperationResult.success().addPayload(encryptedString);
        } catch (Exception e) {
            log.error("{} got exception while encrypting text. Exception message: {}", TAG, e.getMessage());
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    /**
     * Decrypts text.
     *
     * @param encryptedText text to decrypt.
     * @return {@link OperationResult} with decrypted text in payload or with error.
     */
    public OperationResult decrypt(final String encryptedText) {
        try {
            String decryptedText = encryptionUtils.getSymmetricEncryptor().decrypt(encryptedText);
            return OperationResult.success().addPayload(decryptedText);
        } catch (Exception e) {
            log.error("{} got exception while decrypting text. Exception message: {}", TAG, e.getMessage());
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }


}
