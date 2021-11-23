package io.kyberorg.yalsee.core;

import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class SymmetricCryptTool {
    private static final String TAG = "[" + SymmetricCryptTool.class.getSimpleName() + "]";

    private final EncryptionUtils encryptionUtils;

    public SymmetricCryptTool(EncryptionUtils encryptionUtils) {
        this.encryptionUtils = encryptionUtils;
    }

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
