package pm.axe.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pm.axe.Axe;
import pm.axe.result.OperationResult;
import pm.axe.session.AxeSession;
import pm.axe.session.Device;
import pm.axe.utils.AppUtils;

import javax.servlet.http.Cookie;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link AxeSession} Cookie-related methods.
 *
 * @since 3.8
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AxeSessionCookieService {
    private static final String TAG = "[" + AxeSessionCookieService.class.getSimpleName() + "]";
    private static final String ERR_DEVICE_OBJECT_IS_NULL = "Device object is null";
    private static final String ERR_COOKIE_OBJECT_IS_NULL = "Cookie object is null";
    private static final String ERR_COOKIE_VALUE_IS_EMPTY = "Cookie Value is empty";
    private static final String ERR_MALFORMED_COOKIE_VALUE = "Cookie value length mismatch";
    private static final String ERR_RECORD_NOT_FOUND = "No corresponding record found in Redis";
    private static final String ERR_HACK_ATTEMPT = "Record with ID exists, but info not match. Got Stolen Cookie.";
    private static final String ERR_SESSION_EXPIRED = "Session expired";
    private static final String ERR_SESSION_ALMOST_EXPIRED = "Session almost expired";

    private final AxeSessionService axeSessionService;
    private final AppUtils appUtils;

    /**
     * Creates Cookie from {@link AxeSession}.
     *
     * @param ys non-empty {@link AxeSession} to get info from.
     * @return Session Cookie.
     * @throws IllegalArgumentException when {@link AxeSession} is {@code null}.
     */
    public Cookie createCookie(final AxeSession ys) {
        if (ys == null) throw new IllegalArgumentException("AxeSession cannot be null");
        Cookie cookie = new Cookie(Axe.CookieNames.AXE_SESSION, ys.getSessionId());
        cookie.setMaxAge(appUtils.getSessionTimeout());
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
     * {@link OperationResult#OK} status and {@link AxeSession} object if payload.
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
        if (cookieValue.length() != AxeSession.SESSION_ID_LEN) {
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_COOKIE_VALUE);
        }

        //lookup by sessionId
        Optional<AxeSession> sessionRecord = axeSessionService.getSession(cookieValue);
        if (sessionRecord.isEmpty()) {
            return OperationResult.elementNotFound().withMessage(ERR_RECORD_NOT_FOUND);
        }
        AxeSession session = sessionRecord.get();
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
    public void invalidateAffectedSession(final AxeSession session) {
        log.info("{} deleting affected session {}", TAG, session.getSessionId());
        axeSessionService.destroySession(session);
    }
}
