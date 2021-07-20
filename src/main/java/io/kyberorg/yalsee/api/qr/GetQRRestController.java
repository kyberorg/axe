package io.kyberorg.yalsee.api.qr;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.constants.MimeType;
import io.kyberorg.yalsee.json.QRCodeResponse;
import io.kyberorg.yalsee.json.YalseeErrorJson;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.services.QRCodeService;
import io.kyberorg.yalsee.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static io.kyberorg.yalsee.constants.HttpCode.STATUS_400;
import static io.kyberorg.yalsee.constants.HttpCode.STATUS_404;

/**
 * Generates QR from short link.
 *
 * @since 3.1
 */
@Slf4j
@RestController
public class GetQRRestController {
    private static final String TAG = "[" + GetQRRestController.class.getSimpleName() + "]";

    private final QRCodeService qrCodeService;

    /**
     * Constructor for Spring autowiring.
     *
     * @param qrService    service which handles QR codes related actions
     */
    public GetQRRestController(final QRCodeService qrService) {
        this.qrCodeService = qrService;
    }

    /**
     * Provides QR Code with encoded short link.
     * QR Code size: {@link App.QR#DEFAULT_QR_CODE_SIZE}x{@link App.QR#DEFAULT_QR_CODE_SIZE} px.
     *
     * @param ident part of URL, which identifies short link
     * @return {@link ResponseEntity} with {@link QRCodeResponse} or {@link YalseeErrorJson}.
     */
    @GetMapping(value = Endpoint.Api.GET_QR_WITH_IDENT, produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> getQRCode(final @PathVariable("ident") String ident) {
        log.info("{} got GET request: {\"Ident\": {}}", TAG, ident);

        OperationResult createQRCodeResult = qrCodeService.getQRCode(ident);

        if (createQRCodeResult.ok()) {
            log.info("{} created QR Code for {}", TAG, ident);
            QRCodeResponse responseJson = QRCodeResponse.withQRCode(createQRCodeResult.getStringPayload());
            return ResponseEntity.ok(responseJson);
        } else {
            return handleQRCreateFail(createQRCodeResult);
        }
    }

    /**
     * Provides squared QR Code with encoded short link of given size.
     *
     * @param ident part of URL, which identifies short link
     * @param sizeString size in pixels. Minimum: {@link App.QR#MINIMAL_SIZE_IN_PIXELS} px
     * @return {@link ResponseEntity} with {@link QRCodeResponse} or {@link YalseeErrorJson}.
     */
    @GetMapping(value = Endpoint.Api.GET_QR_WITH_IDENT_AND_SIZE,
            produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> getQRCodeWithCustomSize(final @PathVariable("ident") String ident,
                                                     final @PathVariable("size") String sizeString) {
        log.info("{} got GET request: {\"Ident\": {}, \"Size\": {}}", TAG, ident, sizeString);

        int size;
        try {
            size = Integer.parseInt(sizeString);
        } catch (NumberFormatException e) {
            return handleNumberFormatException(QRCodeService.ERR_MALFORMED_SIZE);
        }

        OperationResult createQRCodeResult = qrCodeService.getQRCode(ident, size);

        if (createQRCodeResult.ok()) {
            log.info("{} created QR Code for {}", TAG, ident);
            QRCodeResponse responseJson = QRCodeResponse.withQRCode(createQRCodeResult.getStringPayload());
            return ResponseEntity.ok(responseJson);
        } else {
            return handleQRCreateFail(createQRCodeResult);
        }
    }

    /**
     * Provides QR Code with encoded short link of given width and height.
     *
     * @param ident part of URL, which identifies short link
     * @param widthString width in pixels. Minimum: {@link App.QR#MINIMAL_SIZE_IN_PIXELS} px
     * @param heightString height in pixels. Minimum: {@link App.QR#MINIMAL_SIZE_IN_PIXELS} px
     * @return {@link ResponseEntity} with {@link QRCodeResponse} or {@link YalseeErrorJson}.
     */
    @GetMapping(value = Endpoint.Api.GET_QR_WITH_IDENT_WIDTH_AND_HEIGHT,
            produces = MimeType.APPLICATION_JSON)
    public ResponseEntity<?> getQRCodeWithCustomSize(final @PathVariable("ident") String ident,
                                                     final @PathVariable("width") String widthString,
                                                     final @PathVariable("height") String heightString) {
        log.info("{} got GET request: {\"Ident\": {}, \"Width\": {}, \"Height\": {}}",
                TAG, ident, widthString, heightString);

        int width;
        try {
            width = Integer.parseInt(widthString);
        } catch (NumberFormatException e) {
            return handleNumberFormatException(QRCodeService.ERR_MALFORMED_WIDTH);
        }

        int height;
        try {
            height = Integer.parseInt(heightString);
        } catch (NumberFormatException e) {
            return handleNumberFormatException(QRCodeService.ERR_MALFORMED_HEIGHT);
        }

        OperationResult createQRCodeResult = qrCodeService.getQRCode(ident, width, height);

        if (createQRCodeResult.ok()) {
            log.info("{} created QR Code for {}", TAG, ident);
            QRCodeResponse responseJson = QRCodeResponse.withQRCode(createQRCodeResult.getStringPayload());
            return ResponseEntity.ok(responseJson);
        } else {
            return handleQRCreateFail(createQRCodeResult);
        }
    }

    private ResponseEntity<YalseeErrorJson> handleQRCreateFail(final OperationResult result) {
        switch (result.getResult()) {
            case OperationResult.MALFORMED_INPUT:
                switch (result.getMessage()) {
                    case QRCodeService.ERR_MALFORMED_IDENT:
                        log.info("{} not valid ident", TAG);
                        return ApiUtils.handleIdentFail(result);
                    case QRCodeService.ERR_MALFORMED_SIZE:
                    case QRCodeService.ERR_MALFORMED_WIDTH:
                    case QRCodeService.ERR_MALFORMED_HEIGHT:
                        log.info("{} not valid size/width/height", TAG);
                        YalseeErrorJson errJson = YalseeErrorJson.createWithMessage(result.getMessage())
                                .andStatus(STATUS_400);
                        return ResponseEntity.badRequest().body(errJson);
                    default:
                        return ApiUtils.handleServerError();
                }
            case OperationResult.ELEMENT_NOT_FOUND:
                log.info("{} ident not found", TAG);
                YalseeErrorJson errorJson = YalseeErrorJson.createWithMessage("No links found by this ident. "
                        + "Ident should be stored before requesting QR code")
                        .andStatus(STATUS_404);
                return ResponseEntity.status(STATUS_404).body(errorJson);
            case OperationResult.SYSTEM_DOWN:
                log.error("{} Database is DOWN", TAG);
                return ApiUtils.handleSystemDown();
            case OperationResult.GENERAL_FAIL:
            default:
                log.error("{} Error: {}", TAG, result.getMessage());
                return ApiUtils.handleServerError();
        }
    }

    private ResponseEntity<YalseeErrorJson> handleNumberFormatException(final String message) {
        YalseeErrorJson errorJson = YalseeErrorJson
                .createWithMessage(message)
                .andStatus(STATUS_400);
        return ResponseEntity.badRequest().body(errorJson);
    }
}
