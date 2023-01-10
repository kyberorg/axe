package pm.axe.api.middleware;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pm.axe.Axe;
import pm.axe.result.OperationResult;
import pm.axe.utils.AppUtils;

/**
 * Validates Provided Token.
 *
 * @since 3.0.4
 */
@RequiredArgsConstructor
@Component
public class TokenCheckerMiddleware {
    /**
     * Marker, that indicates that request passed to check is empty (null).
     */
    public static final String REQUEST_IS_EMPTY = "ERR_EMPTY_REQUEST";

    /**
     * Marker, that indicates that there is no token in provided request.
     */
    public static final String NO_TOKEN = "ERR_NO_TOKEN";

    /**
     * Indicates that token in request is present, but doesn't match correct token.
     */
    public static final String WRONG_TOKEN = "ERR_WRONG_TOKEN";

    private final AppUtils appUtils;

    /**
     * Performs Master Token check.
     *
     * @param request {@link HttpServletRequest} with token inside.
     * @return {@link OperationResult#OK} - when token is correct,
     * {@link OperationResult} with one of the markers in message - if token is not valid.
     */
    public OperationResult checkMasterToken(final HttpServletRequest request) {
        if (request == null) return OperationResult.generalFail().withMessage(REQUEST_IS_EMPTY);

        String token = extractToken(request);
        if (StringUtils.isBlank(token)) {
            return OperationResult.malformedInput().withMessage(NO_TOKEN);
        }

        if (token.equals(appUtils.getMasterToken())) {
            return OperationResult.success();
        } else {
            return OperationResult.generalFail().withMessage(WRONG_TOKEN);
        }
    }

    private static String extractToken(final HttpServletRequest request) {
        return request.getHeader(Axe.Headers.X_AXE_TOKEN);
    }


}
