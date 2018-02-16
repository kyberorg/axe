package ee.yals.utils;

import com.google.common.hash.Hashing;
import ee.yals.models.Secret;
import ee.yals.models.User;
import ee.yals.models.dao.SecretDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * Password utils: {@link Password.Encryptor}, {@link Password.Comparator} and {@link Password.Tester}
 *
 * @since 3.0
 */
@Component
public class Password {

    public static final Password.Status EQUAL = Status.EQUAL;
    public static final Password.Status NOT_EQUAL = Status.NOT_EQUAL;

    /**
     * {@link Encryptor} makes encrypted string to store in DB from plain test password.
     * <p>
     * Usage: {@code String encryptedPassword = encryptor.encrypt("myPassword);}
     */
    @Component
    public class Encryptor {
        /**
         * Generates SHA-512 string from plain text
         *
         * @param textToEncrypt plain text for encryption
         * @return string with encrypted hash
         */
        public String encrypt(String textToEncrypt) {
            if (StringUtils.isBlank(textToEncrypt)) {
                //Because empty string gives empty hash
                return "";
            }
            return Hashing.sha512().hashString(textToEncrypt, StandardCharsets.UTF_8).toString();
        }
    }

    /**
     * Compares plain text password with encrypted one.
     * Gives {@link Status#EQUAL} if encrypted version of plain password looks same as given encrypted string.
     * {@link Status#NOT_EQUAL} - elsewhere.
     * <p>
     * Usage: {@code comparator.comparePlain("mySuperPass").withEncrypted("29def235")}
     */
    @Component
    public class Comparator {
        @Autowired
        private Encryptor encryptor;

        private String plainPass;

        private Comparator() {
        }

        public Method2 comparePlain(String plainPassword) {
            this.plainPass = Objects.isNull(plainPassword) ? "" : plainPassword;
            return new Method2();
        }

        /**
         * This is internal class to prevent access to {@link #withEncrypted(String)} method directly from {@link Comparator} object
         */
        public class Method2 {
            public Password.Status withEncrypted(String encrypted) {
                String enc = encryptor.encrypt(plainPass);
                return StringUtils.equalsIgnoreCase(encrypted, enc) ? EQUAL : NOT_EQUAL;
            }
        }
    }

    /**
     * Password {@link Tester} useful for testing password provides by user. It uses {@link Comparator} internally.
     * <p>
     * Usage: {@code tester.test("myPass").forUser(myUser); }
     */
    @Component
    public class Tester {

        @Autowired
        private Comparator comparator;

        @Autowired
        private SecretDao secretDao;

        private String passwordToTest;

        public Method2 test(String password) {
            this.passwordToTest = password;
            return new Method2();
        }

        /**
         * This is internal class to prevent access to {@link #forUser(User)} method directly from {@link Tester} object
         */
        public class Method2 {
            public Status forUser(User user) {
                Optional<Secret> usersPass = secretDao.findSingleByUser(user);
                return usersPass.map(secret -> comparator.comparePlain(passwordToTest).withEncrypted(secret.getPassword()))
                        .orElse(Status.NOT_EQUAL);
            }
        }
    }

    public enum Status {
        EQUAL,
        NOT_EQUAL
    }
}
