package io.kyberorg.yalsee.services;

import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.session.Device;
import io.kyberorg.yalsee.session.YalseeSession;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link YalseeSession} Cookie-related methods.
 *
 * @since 3.8
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class YalseeSessionCookieService {
    private static final String TAG = "[" + YalseeSessionCookieService.class.getSimpleName() + "]";
    private static final String ERR_DEVICE_OBJECT_IS_NULL = "Device object is null";
    private static final String ERR_COOKIE_OBJECT_IS_NULL = "Cookie object is null";
    private static final String ERR_COOKIE_VALUE_IS_EMPTY = "Cookie Value is empty";
    private static final String ERR_MALFORMED_COOKIE_VALUE = "Cookie value length mismatch";
    private static final String ERR_RECORD_NOT_FOUND = "No corresponding record found in Redis";
    private static final String ERR_HACK_ATTEMPT = "Record with ID exists, but info not match. Got Stolen Cookie.";
    private static final String ERR_SESSION_EXPIRED = "Session expired";
    private static final String ERR_SESSION_ALMOST_EXPIRED = "Session almost expired";

    private final YalseeSessionService yalseeSessionService;
    private final AppUtils appUtils;

    /**
     * Creates Cookie from {@link YalseeSession}.
     *
     * @param ys non-empty {@link YalseeSession} to get info from.
     * @return Session Cookie.
     * @throws IllegalArgumentException when {@link YalseeSession} is {@code null}.
     */
    public Cookie createCookie(final YalseeSession ys) {
        if (ys == null) throw new IllegalArgumentException("YalseeSession cannot be null");
        Cookie cookie = new Cookie(App.CookieNames.YALSEE_SESSION, ys.getSessionId());
        cookie.setMaxAge(appUtils.getSessionTimeout() + 10);
        cookie.setSecure(ys.getDevice().isSecureConnection());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * Validates Session Cookie.
     *
     * @param cookie        non-empty Session {@link Cookie} to validate.
     * @param currentDevice Information about current device to compare it to info stored in Cookie's Session.
     * @return {@link OperationResult} with:
     * {@link OperationResult#OK} status and {@link YalseeSession} object if payload.
     * {@link OperationResult#MALFORMED_INPUT} with {@link #ERR_COOKIE_VALUE_IS_EMPTY} message
     * when Cookie Value is blank.
     * {@link OperationResult#MALFORMED_INPUT} with {@link #ERR_MALFORMED_COOKIE_VALUE} message
     * when Cookie Value is not valid session id i.e. has wrong length.
     * {@link OperationResult#ELEMENT_NOT_FOUND} with {@link #ERR_RECORD_NOT_FOUND} message
     * when Session given id is not found.
     * {@link OperationResult#BANNED} with {@link #ERR_SESSION_EXPIRED} message
     * when referred Session is already expired.
     * {@link OperationResult#BANNED} with {@link #ERR_HACK_ATTEMPT} message
     * when Session is accessed from another Device aka stolen cookie.
     * @throws IllegalArgumentException if {@link Cookie} or {@link Device} is {@code null}.
     */
    public OperationResult checkCookie(final Cookie cookie, final Device currentDevice) {
        //Device null check
        if (Objects.isNull(currentDevice)) {
            throw new IllegalArgumentException(ERR_DEVICE_OBJECT_IS_NULL);
        }
        //Cookie Value aka Session Id
        if (Objects.isNull(cookie)) {
            throw new IllegalArgumentException(ERR_COOKIE_OBJECT_IS_NULL);
        }
        String cookieValue = cookie.getValue();
        if (StringUtils.isBlank(cookieValue)) {
            return OperationResult.malformedInput().withMessage(ERR_COOKIE_VALUE_IS_EMPTY);
        }
        if (cookieValue.length() != YalseeSession.SESSION_ID_LEN) {
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_COOKIE_VALUE);
        }

        //lookup by sessionId
        Optional<YalseeSession> sessionRecord = yalseeSessionService.getSession(cookieValue);
        if (sessionRecord.isEmpty()) {
            return OperationResult.elementNotFound().withMessage(ERR_RECORD_NOT_FOUND);
        }
        YalseeSession session = sessionRecord.get();
        //valid (not expired)
        if (session.expired()) {
            invalidateAffectedSession(session);
            return OperationResult.gone().withMessage(ERR_SESSION_EXPIRED);
        }
        //almost dead aka too old
        if (session.isAlmostExpired()) {
            return OperationResult.gone().withMessage(ERR_SESSION_ALMOST_EXPIRED);
        }

        //compare devices
        Device savedDevice = session.getDevice();
        if (!currentDevice.isSameDevice(savedDevice)) {
            //device mismatch - stolen cookie
            this.invalidateAffectedSession(session);
            return OperationResult.banned().withMessage(ERR_HACK_ATTEMPT);
        }

        //all good
        return OperationResult.success().addPayload(session);
    }

    /**
     * Invalidates (deletes) affected session in case of attack or {@link #ERR_HACK_ATTEMPT}.
     *
     * @param session session to delete.
     */
    public void invalidateAffectedSession(final YalseeSession session) {
        log.info("{} deleting affected session {}", TAG, session.getSessionId());
        yalseeSessionService.destroySession(session);
    }
}
