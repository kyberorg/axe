package io.kyberorg.yalsee.services.user;

import com.vaadin.flow.server.WebBrowser;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.models.Login;
import io.kyberorg.yalsee.models.User;
import io.kyberorg.yalsee.models.dao.LoginDao;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

/**
 * Persistent Logins (aka Remember Me Cookie).
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {
    public static final String PK_NEW_COOKIE = "NewCookie";
    public static final String PK_COOKIE_USER = "CookieUser";

    private static final String TAG = "[" + LoginService.class.getSimpleName() + "]";
    private static final String ERR_COOKIE_OBJECT_IS_NULL = "Cookie object is null";
    private static final String ERR_COOKIE_VALUE_IS_EMPTY = "Cookie Value is empty";
    private static final String ERR_MALFORMED_COOKIE_VALUE = "Cookie value should have 3 parts";
    private static final String ERR_HACK_ATTEMPT = "Series matches, but Token not. It is stolen token.";
    private static final String ERR_EMPTY_OR_CORRUPTED_SARJA = "Got empty or corrupted sarja";
    private static final String ERR_RECORD_NOT_FOUND = "No corresponding record found in DB";
    private static final String ERR_TOKEN_UPDATE_FAILED = "Token update failed";
    private static final String ERR_LOGIN_EXPIRED = "Login expired";

    private static final int SARJA_LENGTH = 15;
    private static final int TOKEN_LENGTH = 12;

    private static final String DEFAULT_USER_AGENT = "Unknown Browser";
    private static final String DEFAULT_IP = "0.0.0.0";

    private final LoginDao loginDao;
    private final AppUtils appUtils;

    public OperationResult createNewLoginRecord(final User user, final WebBrowser webBrowser) {

        String sarja;
        do {
            sarja = RandomStringUtils.randomAlphanumeric(SARJA_LENGTH);
        } while (loginDao.existsBySarja(sarja));

        String token;
        do {
            token = RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
        } while (loginDao.existsByToken(token));

        DeviceInfo deviceInfo = getDeviceInfo(webBrowser);

        Login newLogin = new Login();
        newLogin.setUser(user);
        newLogin.setSarja(sarja);
        newLogin.setToken(token);
        newLogin.setUserAgent(deviceInfo.getUserAgent());
        newLogin.setIp(deviceInfo.getIp());
        newLogin.setNotValidAfter(addLoginTimeout(newLogin.getCreated()));

        try {
            loginDao.save(newLogin);
            return OperationResult.success().addPayload(newLogin);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to save login record for user {}", TAG, user);
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    public String constructCookieValue(final Login loginRecord) {
        if (loginRecord == null || loginRecord.getId() == null) {
            return null;
        }
        if (loginRecord.getUser() == null || loginRecord.getUser().isLocked()) {
            return null;
        }

        String sarja = loginRecord.getSarja();
        String hashedToken = DigestUtils.sha256Hex(loginRecord.getToken());

        return String.join(App.COOKIE_SEPARATOR, sarja, hashedToken);
    }

    public OperationResult loginWithCookie(final Cookie cookie, final WebBrowser webBrowser) {
        OperationResult cookiePartsResult = getCookieParts(cookie);
        if (cookiePartsResult.notOk()) return cookiePartsResult;
        Parts parts = cookiePartsResult.getPayload(Parts.class);

        //params validation
        if (StringUtils.isBlank(parts.getSarja()) && parts.getSarja().length() == SARJA_LENGTH) {
            return OperationResult.malformedInput().withMessage(ERR_EMPTY_OR_CORRUPTED_SARJA);
        }

        Optional<Login> loginRecord = loginDao.findBySarja(parts.getSarja());
        if (loginRecord.isEmpty()) {
            return OperationResult.elementNotFound().withMessage(ERR_RECORD_NOT_FOUND);
        }

        if (!loginRecord.get().isValid()) {
            this.invalidateAffectedLogin(loginRecord.get());
            return OperationResult.banned().withMessage(ERR_LOGIN_EXPIRED);
        }

        String hashedTokenFromDatabase = DigestUtils.sha256Hex(loginRecord.get().getToken());
        boolean tokenHashedEquals = parts.getHashedToken().equals(hashedTokenFromDatabase);
        if (!tokenHashedEquals) {
            this.invalidateAffectedLogin(loginRecord.get());
            return OperationResult.banned().withMessage(ERR_HACK_ATTEMPT);
        }

        DeviceInfo currentDevice = getDeviceInfo(webBrowser);
        DeviceInfo savedDevice = new DeviceInfo(loginRecord.get().getUserAgent(), loginRecord.get().getIp());
        if (! currentDevice.isSameDevice(savedDevice)) {
            //device mismatch - stolen cookie
            this.invalidateAffectedLogin(loginRecord.get());
            return OperationResult.banned().withMessage(ERR_HACK_ATTEMPT);
        }

        //all good - consider authenticated
        OperationResult tokenUpdateResult = updateToken(loginRecord.get());
        if (tokenUpdateResult.ok()) {
            String newCookieValue = constructCookieValue(tokenUpdateResult.getPayload(Login.class));

            OperationResult result = OperationResult.success();
            result.addPayload(PK_NEW_COOKIE, newCookieValue);
            result.addPayload(PK_COOKIE_USER, loginRecord.get().getUser());
            return result;
        } else {
            log.warn("{} failed to update token for login {}. Reason: {}", TAG, loginRecord, tokenUpdateResult);
            return OperationResult.generalFail().withMessage(ERR_TOKEN_UPDATE_FAILED);
        }
    }

    public Cookie createCookie(final String cookieValue, final WebBrowser webBrowser) {
        Cookie cookie = new Cookie(App.CookieNames.LOGIN_COOKIE, cookieValue);
        cookie.setMaxAge(appUtils.getLoginTimeout());
        cookie.setSecure(webBrowser.isSecureConnection());
        cookie.setHttpOnly(true);
        return cookie;
    }

    public void invalidateAffectedLogin(final Login loginRecord) {
        try {
            loginDao.delete(loginRecord);
        } catch (Exception e) {
            log.error("{} failed to delete affected login record for user {}",
                    TAG, loginRecord.getUser().getUsername());
            log.debug("", e);
        }
    }

    private OperationResult updateToken(final Login loginRecord) {
        try {
            loginRecord.setToken(RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH));
            loginDao.save(loginRecord);
            return OperationResult.success().addPayload(loginRecord);
        } catch (CannotCreateTransactionException c) {
            return OperationResult.databaseDown();
        } catch (Exception e) {
            log.error("{} failed to save login record for user {}", TAG, loginRecord.getUser().getUsername());
            log.debug("", e);
            return OperationResult.generalFail().withMessage(e.getMessage());
        }
    }

    public OperationResult invalidateCurrentCookie(Cookie cookie) {
        OperationResult cookiePartsResult = getCookieParts(cookie);
        if (cookiePartsResult.notOk()) return cookiePartsResult;
        Parts parts = cookiePartsResult.getPayload(Parts.class);

        Optional<Login> loginRecord = loginDao.findBySarja(parts.getSarja());
        if (loginRecord.isPresent()) {
            loginDao.delete(loginRecord.get());
            return OperationResult.success();
        } else {
            return OperationResult.elementNotFound().withMessage(ERR_RECORD_NOT_FOUND);
        }
    }

    private OperationResult getCookieValue(final Cookie cookie) {
        if (Objects.isNull(cookie)) {
            return OperationResult.malformedInput().withMessage(ERR_COOKIE_OBJECT_IS_NULL);
        }
        String cookieValue = cookie.getValue();
        if (StringUtils.isBlank(cookieValue)) {
            return OperationResult.malformedInput().withMessage(ERR_COOKIE_VALUE_IS_EMPTY);
        }
        return OperationResult.success().addPayload(cookieValue);
    }

    private OperationResult splitCookieValue(final String cookieValue) {
        String[] partsArr = cookieValue.split(App.COOKIE_SEPARATOR);
        if (partsArr.length != 2) {
            log.warn("{} Cookie value should have 2 parts. Got {}. Value: {}", TAG, partsArr.length, cookieValue);
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_COOKIE_VALUE);
        }
        final Parts parts = new Parts(partsArr[0], partsArr[1]);
        return OperationResult.success().addPayload(parts);
    }

    private OperationResult getCookieParts(final Cookie cookie) {
        OperationResult getCookieValueResult = getCookieValue(cookie);
        if (getCookieValueResult.notOk()) return getCookieValueResult;

        String cookieValue = getCookieValueResult.getStringPayload();
        return splitCookieValue(cookieValue);
    }

    private DeviceInfo getDeviceInfo(final WebBrowser webBrowser) {
        DeviceInfo deviceInfo;
        if (webBrowser == null) {
            deviceInfo = DeviceInfo.withDefaults();
        } else {
            String userAgent = webBrowser.getBrowserApplication();
            String ip = webBrowser.getAddress();

            deviceInfo = new DeviceInfo();
            if (StringUtils.isNotBlank(userAgent)) {
                deviceInfo.setUserAgent(userAgent);
            }
            if (StringUtils.isNotBlank(ip)) {
                deviceInfo.setIp(ip);
            }
        }
        return deviceInfo;
    }

    private Timestamp addLoginTimeout(Timestamp now) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.SECOND, appUtils.getLoginTimeout());
        return new Timestamp(cal.getTime().getTime());
    }

    @Data
    private static class Parts {
        private final String sarja;
        private final String hashedToken;
    }

    @Data
    private static class DeviceInfo {
        private String userAgent = DEFAULT_USER_AGENT;
        private String ip = DEFAULT_IP;

        public static DeviceInfo withDefaults() {
            return new DeviceInfo();
        }

        public DeviceInfo(final String ua, final String ip) {
            this.userAgent = ua;
            this.ip = ip;
        }

        public DeviceInfo() {}

        public boolean isSameDevice(DeviceInfo other) {
            return userAgent.equals(other.getUserAgent()) && ip.equals(other.ip);
        }
    }

}
