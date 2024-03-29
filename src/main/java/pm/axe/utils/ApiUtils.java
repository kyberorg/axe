package pm.axe.utils;

import kong.unirest.HttpStatus;
import org.springframework.http.ResponseEntity;
import pm.axe.Endpoint;
import pm.axe.api.middleware.TokenCheckerMiddleware;
import pm.axe.core.IdentValidator;
import pm.axe.json.AxeErrorJson;
import pm.axe.result.OperationResult;

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
     * @return {@link ResponseEntity} with {@link AxeErrorJson} inside
     */
    public static ResponseEntity<AxeErrorJson> handleSystemDown() {
        AxeErrorJson errorJson = AxeErrorJson.createWithMessage("Application is DOWN")
                .andStatus(HttpStatus.SERVICE_UNAVAILABLE);
        return ResponseEntity.status(errorJson.getStatus()).body(errorJson);
    }

    /**
     * Creates standard reply when some error occurs at server side.
     *
     * @return {@link ResponseEntity} with {@link AxeErrorJson} inside
     */
    public static ResponseEntity<AxeErrorJson> handleServerError() {
        AxeErrorJson errorJson = AxeErrorJson.createWithMessage("Something wrong at our side")
                .andStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorJson);
    }

    /**
     * Constructs {@link ResponseEntity} with {@link AxeErrorJson} inside. Uses user-defined message and status.
     *
     * @param httpStatus int with HTTP Status. See {@link HttpStatus}.
     * @param message    string with error message.
     * @return {@link ResponseEntity} with {@link AxeErrorJson} inside
     */
    public static ResponseEntity<AxeErrorJson> handleError(final int httpStatus, final String message) {
        AxeErrorJson errorJson = AxeErrorJson.createWithMessage(message).andStatus(httpStatus);
        return ResponseEntity.status(httpStatus).body(errorJson);
    }

    /**
     * Constructs {@link ResponseEntity} with {@link AxeErrorJson} inside.
     * Uses user-defined status and message from {@link OperationResult}.
     *
     * @param httpStatus int with HTTP Status. See {@link HttpStatus}.
     * @param opResult   operation result to get message from.
     * @return {@link ResponseEntity} with {@link AxeErrorJson} inside
     */
    public static ResponseEntity<AxeErrorJson> handleError(final int httpStatus, final OperationResult opResult) {
        AxeErrorJson errorJson = AxeErrorJson.createWithMessage(opResult.getMessage()).andStatus(httpStatus);
        return ResponseEntity.status(httpStatus).body(errorJson);
    }

    /**
     * Handles negative token check result.
     *
     * @param checkResult check result for analysis.
     * @return ready to pass {@link ResponseEntity} with {@link AxeErrorJson}
     */
    public static ResponseEntity<AxeErrorJson> handleTokenFail(final OperationResult checkResult) {
        String errorMarker = checkResult.getResult();
        AxeErrorJson errorJson = switch (errorMarker) {
            case TokenCheckerMiddleware.REQUEST_IS_EMPTY ->
                    AxeErrorJson.createWithMessage("Got empty request").andStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            case TokenCheckerMiddleware.NO_TOKEN ->
                    AxeErrorJson.createWithMessage("Unauthorized: Auth Token must be present")
                    .andStatus(HttpStatus.UNAUTHORIZED);
            default -> AxeErrorJson.createWithMessage("Unauthorized: Wrong Token")
                    .andStatus(HttpStatus.UNAUTHORIZED);
        };
        return ResponseEntity.status(errorJson.getStatus()).body(errorJson);
    }

    /**
     * Handles negative ident validation result.
     *
     * @param validateResult check result for analysis.
     * @return ready to pass {@link ResponseEntity} with {@link AxeErrorJson}
     */
    public static ResponseEntity<AxeErrorJson> handleIdentFail(final OperationResult validateResult) {
        String errorReason = validateResult.getMessage();
        AxeErrorJson errorJson;
        if (errorReason.equals(IdentValidator.EMPTY_IDENT)) {
            errorJson = AxeErrorJson.createWithMessage("Request should be like this: "
                            + Endpoint.Api.LINKS_API_PLUS_IDENT + " and ident should not be empty")
                    .andStatus(HttpStatus.BAD_REQUEST);
        } else {
            errorJson = AxeErrorJson.createWithMessage("Ident must be 2+ chars alphabetic string")
                    .andStatus(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.badRequest().body(errorJson);
    }
}
