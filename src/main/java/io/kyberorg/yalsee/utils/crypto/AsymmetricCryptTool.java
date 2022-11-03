package io.kyberorg.yalsee.utils.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsymmetricCryptTool {
    private final EncryptionUtils encryptionUtils;

    public String encrypt(final String plainText) {
        return encryptionUtils.getAsymmetricEncoder().encode(plainText);
    }

    public boolean compare(final String encryptedText, final String plainText) {
        return encryptionUtils.getAsymmetricEncoder().encode(plainText).equals(encryptedText);
    }
}
