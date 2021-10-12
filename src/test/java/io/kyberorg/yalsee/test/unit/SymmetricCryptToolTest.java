package io.kyberorg.yalsee.test.unit;

import io.kyberorg.yalsee.core.SymmetricCryptTool;
import io.kyberorg.yalsee.result.OperationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SymmetricCryptToolTest extends UnitTest {

    @Autowired
    private SymmetricCryptTool symmetricCryptTool;

    @Test
    void whenEncryptedAndDecryptedToSameString_thenSuccess() {
        String plainText = "plainTextString";
        OperationResult encryptionResult = symmetricCryptTool.encrypt(plainText);
        assertNotNull(encryptionResult);
        assertTrue(encryptionResult.ok(), "Encryption failed");
        assertNotNull(encryptionResult.getStringPayload());

        String encryptedString = encryptionResult.getStringPayload();

        OperationResult decryptionResult = symmetricCryptTool.decrypt(encryptedString);
        assertNotNull(decryptionResult);
        assertTrue(decryptionResult.ok(), "Decryption failed");
        assertNotNull(decryptionResult.getStringPayload());

        assertEquals(plainText, decryptionResult.getStringPayload());
    }

}