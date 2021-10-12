package io.kyberorg.yalsee.core;

import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Slf4j
@Component
public final class SymmetricCryptTool {
    private static final String TAG = "[" + SymmetricCryptTool.class.getSimpleName() + "]";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;

    private static final String algorithm = "AES/CBC/PKCS5Padding";

    private final EncryptionUtils encryptionUtils;

    private final SecretKey secretKey;
    private final IvParameterSpec iv;

    public SymmetricCryptTool(EncryptionUtils encryptionUtils)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.encryptionUtils = encryptionUtils;

        secretKey = generateKey();
        iv = generateIv();
    }

    public OperationResult encrypt(String plainText) {
        try {
            String encryptedString = encrypt(plainText, secretKey, iv);
            return OperationResult.success().addPayload(encryptedString);
        } catch (Exception e) {
            log.error("{} got exception while encrypting text. Exception message: {}", TAG, e.getMessage());
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    public OperationResult decrypt(String encryptedText) {
        try {
            String plainText = decrypt(encryptedText, secretKey, iv);
            return OperationResult.success().addPayload(plainText);
        } catch (Exception e) {
            log.error("{} got exception while decrypting text. Exception message: {}", TAG, e.getMessage());
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    private SecretKey generateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String serverSecretKey = encryptionUtils.getServerKey();
        String salt = encryptionUtils.getPasswordSalt();

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(serverSecretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private String encrypt(String input, SecretKey key,
                           IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(SymmetricCryptTool.algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    private String decrypt(String cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(SymmetricCryptTool.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

}
