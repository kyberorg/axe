package io.kyberorg.yalsee.utils;

import io.kyberorg.yalsee.core.IdentValidator;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import org.springframework.http.ResponseEntity;

import static io.kyberorg.yalsee.constants.HttpCode.*;

public class ApiUtils {

    public static ResponseEntity<YalseeErrorJson> handleSystemDown() {
        YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("Application is DOWN").andStatus(STATUS_503);
        return ResponseEntity.status(errorJson.getStatus()).body(errorJson);
    }

    public static ResponseEntity<YalseeErrorJson> handleServerError() {
        YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("Something wrong at our side")
                .andStatus(STATUS_500);
        return ResponseEntity.status(STATUS_500).body(errorJson);
    }

    public static ResponseEntity<YalseeErrorJson> handleTokenFail(final OperationResult checkResult) {
        String errorMarker = checkResult.getResult();
        YalseeErrorJson errorJson;
        switch (errorMarker) {
            case TokenChecker.REQUEST_IS_EMPTY:
               errorJson = YalseeErrorJson.createWithMessage("Got empty request").andStatus(STATUS_500);
               break;
            case TokenChecker.NO_TOKEN:
                errorJson = YalseeErrorJson.createWithMessage("Unauthorized: Auth Token must be present")
                        .andStatus(STATUS_401);
                break;
            case TokenChecker.WRONG_TOKEN:
            default:
                errorJson = YalseeErrorJson.createWithMessage("Unauthorized: Wrong Token").andStatus(STATUS_401);
        }
        return ResponseEntity.status(errorJson.getStatus()).body(errorJson);
    }

    public static ResponseEntity<YalseeErrorJson> handleIdentFail(final OperationResult validateResult) {
        String errorReason = validateResult.getResult();
        YalseeErrorJson errorJson;
        switch (errorReason) {
            case IdentValidator.EMPTY_IDENT:
                errorJson = YalseeErrorJson.createWithMessage("Request should be like this: " +
                        "Endpoint.Api.DELETE_LINKS_API and ident should not be empty")
                        .andStatus(STATUS_400);
                break;
            case IdentValidator.MALFORMED_IDENT:
            default:
                errorJson = YalseeErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string")
                        .andStatus(STATUS_400);
        }
        return ResponseEntity.badRequest().body(errorJson);
    }
}
