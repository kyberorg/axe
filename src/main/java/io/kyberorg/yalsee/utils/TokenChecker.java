package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.result.OperationResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Validates Provided Token.
 *
 * @since 3.0.4
 */
@RequiredArgsConstructor
@Component
public class TokenChecker {
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
     * Performs token check.
     *
     * @param request {@link HttpServletRequest} with token inside.
     * @return {@link OperationResult#OK} - when token is correct,
     * {@link OperationResult} with one of the markers in message - if token is not valid.
     */
    public OperationResult check(final HttpServletRequest request) {
        if (request == null) return OperationResult.generalFail().withMessage(REQUEST_IS_EMPTY);
        String token = extractToken(request);
        if (StringUtils.isBlank(token)) {
            return OperationResult.malformedInput().withMessage(NO_TOKEN);
        }

        if (token.equals(appUtils.getDeleteToken())) {
            return OperationResult.success();
        } else {
            return OperationResult.generalFail().withMessage(WRONG_TOKEN);
        }
    }

    private static String extractToken(final HttpServletRequest request) {
        return request.getHeader(Header.X_YALSEE_TOKEN);
    }


}
