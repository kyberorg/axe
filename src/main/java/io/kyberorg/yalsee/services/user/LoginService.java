package io.kyberorg.yalsee.services.user;

import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.Optional;

/**
 * Persistent Logins (aka Remember Me Cookie).
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {
    private static final String TAG = "[" + LoginService.class.getSimpleName() + "]";
    private static final String ERR_COOKIE_OBJECT_IS_NULL = "Cookie object is null";
    private static final String ERR_COOKIE_VALUE_IS_EMPTY = "Cookie Value is empty";
    private static final String ERR_FAILED_TO_DECRYPT_COOKIE = "Failed to decrypt cookie";
    private static final String ERR_MALFORMED_COOKIE_VALUE = "Cookie value should have 3 parts";
    private static final String ERR_MALFORMED_USER_ID = "Cookie value has malformed user id";
    private static final String ERR_USER_NOT_FOUND = "User with id from Cookie Value does not exist";
    private static final Object ERR_VALUES_NOT_MATCH = "Series and Token are not match stored values";
    private static final String ERR_HACK_ATTEMPT = "Series matches, but Token not. It is stolen token.";

    private final EncryptionUtils encryptionUtils;
    private final UserService userService;

    //TODO replace with DB
    private final String SERIES = "ABC";
    private final String TOKEN = "DEF";
    //TODO end

    public String constructCookieValue(final User user) {
        if (user == null || user.getId() == null || user.isLocked()) {
            return null;
        }
        String cookieValue = String.join(App.URL_SAFE_SEPARATOR, user.getId() + "", SERIES, TOKEN);
        return encryptionUtils.getEasySymmetricEncryptor().encrypt(cookieValue);
    }

    public OperationResult loginWithCookie(final Cookie cookie) {
        // inputs
        if (Objects.isNull(cookie)) {
            return OperationResult.malformedInput().withMessage(ERR_COOKIE_OBJECT_IS_NULL);
        }
        String cookieValue = cookie.getValue();
        if (StringUtils.isBlank(cookieValue)) {
            return OperationResult.malformedInput().withMessage(ERR_COOKIE_VALUE_IS_EMPTY);
        }
        // decrypt
        String rawCookieValue;
        try {
            rawCookieValue = encryptionUtils.getEasySymmetricEncryptor().decrypt(cookieValue);
        } catch (Exception e) {
            log.warn("{} Cookie decryption failed. Got exception {}", TAG, e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("", e);
            }
            return OperationResult.generalFail().withMessage(ERR_FAILED_TO_DECRYPT_COOKIE);
        }

        // splitting
        String[] parts = rawCookieValue.split(App.URL_SAFE_SEPARATOR);
        if (parts.length != App.THREE) {
            log.warn("{} Cookie value should have 3 parts. Got {}. Value: {}", TAG, parts.length, cookieValue);
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_COOKIE_VALUE);
        }

        final String userIdString = parts[0];
        final String series = parts[1];
        final String token = parts[2];

        //params validation
        long userId;
        try {
            userId = Long.parseLong(userIdString);
        } catch (NumberFormatException e) {
            log.warn("{} cannot parse user id. Value to parse: '{}'", TAG, userIdString);
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_USER_ID);
        }

        //TODO validate series and token
        Optional<User> optionalUser = userService.getById(userId);
        if (optionalUser.isEmpty()) {
            log.warn("{} user with id {} not exist", TAG, userId);
            return OperationResult.elementNotFound().withMessage(ERR_USER_NOT_FOUND);
        }

        //TODO check against DAO once ready
        //TODO user = user in DB

        boolean seriesEquals = SERIES.equals(series);
        boolean tokenEquals = TOKEN.equals(token);

        if (seriesEquals && tokenEquals) {
            updateToken();
            return OperationResult.success().addPayload(optionalUser.get());
        } else if (seriesEquals) {
            invalidateAllUsersLogins(optionalUser.get());
            return OperationResult.banned().withMessage(ERR_HACK_ATTEMPT);
        } else {
            return OperationResult.malformedInput().addPayload(ERR_VALUES_NOT_MATCH);
        }
    }

    public OperationResult invalidateAllUsersLogins(final User user) {
        //TODO implement
        return OperationResult.success();
    }

    private void updateToken() {
        //TODO implement once db ready
    }
}
