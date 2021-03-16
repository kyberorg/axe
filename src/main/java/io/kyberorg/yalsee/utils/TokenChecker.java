package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.constants.Header;
import io.kyberorg.yalsee.result.OperationResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class TokenChecker {
    public static final String REQUEST_IS_EMPTY = "ERR_EMPTY_REQUEST";
    public static final String NO_TOKEN = "ERR_NO_TOKEN";
    public static final String WRONG_TOKEN = "ERR_WRONG_TOKEN";

    private final AppUtils appUtils;

    public TokenChecker(AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    public OperationResult check(HttpServletRequest request) {
        if (request == null) return OperationResult.generalFail().withMessage(REQUEST_IS_EMPTY);
        String token = extractToken(request);
        if (StringUtils.isBlank(token)) { return OperationResult.malformedInput().withMessage(NO_TOKEN);}

        if (token.equals(appUtils.getDeleteToken())) {
            return OperationResult.success();
        } else {
            return OperationResult.generalFail().withMessage(WRONG_TOKEN);
        }
    }

    private static String extractToken(HttpServletRequest request) {
        return request.getHeader(Header.X_YALSEE_TOKEN);
    }


}
