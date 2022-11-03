package io.kyberorg.yalsee.test.unit;

import io.kyberorg.yalsee.utils.crypto.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PasswordUtilsTest {

    @Autowired
    private PasswordUtils passwordUtils;

    @Test
    void passwordUtilsShouldEncryptPassword() {
        String encryptedPassword = passwordUtils.encryptPassword("SalaSana");
        assertTrue(StringUtils.isNotBlank(encryptedPassword));
    }

    @Test
    void ifEncryptedPassMatches_thenSuccess() {
        String plainPassword = "MinuSalaSana";
        String encryptedPassword = passwordUtils.encryptPassword(plainPassword);
        //password matcher does encryption magic by itself
        assertTrue(passwordUtils.passwordMatches(encryptedPassword, plainPassword));
    }
}
