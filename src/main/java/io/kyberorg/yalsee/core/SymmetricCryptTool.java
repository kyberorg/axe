package io.kyberorg.yalsee.core;

import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.utils.Utils;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Properties;

@Slf4j
@Component
public final class SymmetricCryptTool {
    private static final String TAG = "[" + SymmetricCryptTool.class.getSimpleName() + "]";
    private static final int IV_BYTES = 16;

    private static final String AES = "AES";
    private static final String algorithm = "AES/CBC/PKCS5Padding";

    private final EncryptionUtils encryptionUtils;

    private final SecretKeySpec secretKey;
    private final IvParameterSpec iv;

    public SymmetricCryptTool(EncryptionUtils encryptionUtils) {
        this.encryptionUtils = encryptionUtils;

        secretKey = generateKey();
        iv = generateIv();
    }

    public OperationResult encrypt(final String plainText) {
        try {
            String encryptedString = doEncrypt(plainText);
            return OperationResult.success().addPayload(encryptedString);
        } catch (Exception e) {
            log.error("{} got exception while encrypting text. Exception message: {}", TAG, e.getMessage());
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    public OperationResult decrypt(final String encryptedText) {
        try {
            String decryptedText = doDecrypt(encryptedText);
            return OperationResult.success().addPayload(decryptedText);
        } catch (Exception e) {
            log.error("{} got exception while decrypting text. Exception message: {}", TAG, e.getMessage());
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    private SecretKeySpec generateKey() {
        String serverSecretKey = encryptionUtils.getServerKey();
        return new SecretKeySpec(getUTF8Bytes(serverSecretKey), AES);
    }

    private IvParameterSpec generateIv() {
        String serverSecretKey = encryptionUtils.getServerKey();
        String sixteenChars;
        if (serverSecretKey.length() < IV_BYTES) {
            int remaining = IV_BYTES - serverSecretKey.length();
            sixteenChars = serverSecretKey + "0".repeat(remaining);
        } else if (serverSecretKey.length() > IV_BYTES) {
            sixteenChars = serverSecretKey.substring(0, IV_BYTES);
        } else {
            sixteenChars = serverSecretKey;
        }

        return new IvParameterSpec(getUTF8Bytes(sixteenChars));
    }

    private String doEncrypt(String text) throws BadPaddingException, IllegalBlockSizeException,
            IOException, InvalidAlgorithmParameterException, InvalidKeyException, ShortBufferException {

        Properties properties = new Properties();
        final ByteBuffer outBuffer;
        final int bufferSize = 1024;
        final int updateBytes;
        final int finalBytes;

        CryptoCipher cipher = Utils.getCipherInstance(algorithm, properties);
        ByteBuffer inBuffer = ByteBuffer.allocateDirect(bufferSize);
        outBuffer = ByteBuffer.allocateDirect(bufferSize);
        inBuffer.put(getUTF8Bytes(text));

        inBuffer.flip(); //ready for the cipher to read it
        // Show the data is there
        log.debug("{} inBuffer={}", TAG, asString(inBuffer));

        // Initializes the cipher with ENCRYPT_MODE,key and iv.
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        // Continues a multiple-part encryption/decryption operation for byte buffer.
        updateBytes = cipher.update(inBuffer, outBuffer);
        log.debug("{} updateBytes={}", TAG, updateBytes);

        // We should call do final at the end of encryption/decryption.
        finalBytes = cipher.doFinal(inBuffer, outBuffer);
        log.debug("{} finalBytes={}", TAG, finalBytes);

        outBuffer.flip(); // ready for use as decrypt
        byte[] encoded = new byte[updateBytes + finalBytes];
        outBuffer.duplicate().get(encoded);
        String encodedString = Base64Utils.encodeToString(encoded);
        log.debug("{} encodedString={}", TAG, encodedString);
        return encodedString;

    }

    private String doDecrypt(String encodedString) throws InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, IOException, InvalidKeyException, ShortBufferException {
        Properties properties = new Properties();
        final ByteBuffer outBuffer;
        final int bufferSize = 1024;
        ByteBuffer decoded = ByteBuffer.allocateDirect(bufferSize);
        //Creates a CryptoCipher instance with the transformation and properties.
        CryptoCipher decipher = Utils.getCipherInstance(algorithm, properties);
        decipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        outBuffer = ByteBuffer.allocateDirect(bufferSize);
        outBuffer.put(Base64Utils.decode(getUTF8Bytes(encodedString)));
        outBuffer.flip();
        decipher.update(outBuffer, decoded);
        decipher.doFinal(outBuffer, decoded);
        decoded.flip(); // ready for use
        log.debug("{} decoded={}", TAG, asString(decoded));

        return asString(decoded);
    }

    private byte[] getUTF8Bytes(final String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    private String asString(ByteBuffer buffer) {
        final ByteBuffer copy = buffer.duplicate();
        final byte[] bytes = new byte[copy.remaining()];
        copy.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
