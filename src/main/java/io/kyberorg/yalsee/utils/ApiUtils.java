package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.core.IdentValidator;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import org.springframework.http.ResponseEntity;

/**
 * API tools and helper methods.
 */
public final class ApiUtils {
    private ApiUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates standard reply when system is down or partially available.
     *
     * @return {@link ResponseEntity} with {@link YalseeErrorJson} inside
     */
    public static ResponseEntity<YalseeErrorJson> handleSystemDown() {
        YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("Application is DOWN")
                .andStatus(HttpCode.APP_IS_DOWN);
        return ResponseEntity.status(errorJson.getStatus()).body(errorJson);
    }

    /**
     * Creates standard reply when some error occurs at server side.
     *
     * @return {@link ResponseEntity} with {@link YalseeErrorJson} inside
     */
    public static ResponseEntity<YalseeErrorJson> handleServerError() {
        YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("Something wrong at our side")
                .andStatus(HttpCode.SERVER_ERROR);
        return ResponseEntity.status(HttpCode.SERVER_ERROR).body(errorJson);
    }

    /**
     * Handles negative token check result.
     *
     * @param checkResult check result for analysis.
     * @return ready to pass {@link ResponseEntity} with {@link YalseeErrorJson}
     */
    public static ResponseEntity<YalseeErrorJson> handleTokenFail(final OperationResult checkResult) {
        String errorMarker = checkResult.getResult();
        YalseeErrorJson errorJson = switch (errorMarker) {
            case TokenChecker.REQUEST_IS_EMPTY ->
                    YalseeErrorJson.createWithMessage("Got empty request").andStatus(HttpCode.SERVER_ERROR);
            case TokenChecker.NO_TOKEN -> YalseeErrorJson.createWithMessage("Unauthorized: Auth Token must be present")
                    .andStatus(HttpCode.UNAUTHORIZED);
            default -> YalseeErrorJson.createWithMessage("Unauthorized: Wrong Token")
                    .andStatus(HttpCode.UNAUTHORIZED);
        };
        return ResponseEntity.status(errorJson.getStatus()).body(errorJson);
    }

    /**
     * Handles negative ident validation result.
     *
     * @param validateResult check result for analysis.
     * @return ready to pass {@link ResponseEntity} with {@link YalseeErrorJson}
     */
    public static ResponseEntity<YalseeErrorJson> handleIdentFail(final OperationResult validateResult) {
        String errorReason = validateResult.getMessage();
        YalseeErrorJson errorJson;
        if (errorReason.equals(IdentValidator.EMPTY_IDENT)) {
            errorJson = YalseeErrorJson.createWithMessage("Request should be like this: "
                            + Endpoint.Api.LINKS_API_PLUS_IDENT + " and ident should not be empty")
                    .andStatus(HttpCode.BAD_REQUEST);
        } else {
            errorJson = YalseeErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string")
                    .andStatus(HttpCode.BAD_REQUEST);
        }
        return ResponseEntity.badRequest().body(errorJson);
    }
}
