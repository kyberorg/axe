package io.kyberorg.yalsee.controllers.rest;

import com.google.zxing.WriterException;
import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.constants.HttpCode;
import io.kyberorg.yalsee.json.EmptyJson;
import io.kyberorg.yalsee.json.QRCodeResponseJson;
import io.kyberorg.yalsee.json.YalsErrorJson;
import io.kyberorg.yalsee.json.YalsJson;
import io.kyberorg.yalsee.result.GetResult;
import io.kyberorg.yalsee.services.LinkService;
import io.kyberorg.yalsee.services.QRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Generates QR from short link.
 *
 * @since 2.6
 */
@Slf4j
@RestController
public class QRCodeRestController {
    private static final String TAG = "[" + QRCodeRestController.class.getSimpleName() + "]";

    private final QRCodeService qrCodeService;
    private final LinkService linkService;

    private HttpServletResponse response;

    /**
     * Constructor for Spring autowiring.
     *
     * @param qrService    service which handles QR codes related actions
     * @param linksService service which handles links
     */
    public QRCodeRestController(final QRCodeService qrService, final LinkService linksService) {
        this.qrCodeService = qrService;
        this.linkService = linksService;
    }

    /**
     * Endpoint for getting QR codes based on ident.
     *
     * @param ident part of URL, which identifies  short link
     * @param resp  HTTP response
     * @return json with reply
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Api.QR_CODE_API + "/{ident}")
    @ResponseBody
    public YalsJson getQRCode(final @PathVariable("ident") String ident, final HttpServletResponse resp) {
        this.response = resp;

        YalsJson testResult = testIdentExist(ident);
        if (testResult instanceof YalsErrorJson) {
            return testResult;
        }

        Optional<String> qrCode = getQRCode(ident, QRCodeService.DEFAULT_SIZE);
        YalsJson result;
        if (qrCode.isPresent()) {
            resp.setStatus(HttpCode.STATUS_200);
            result = QRCodeResponseJson.withQRCode(qrCode.get());
        } else {
            resp.setStatus(HttpCode.STATUS_500);
            result = YalsErrorJson.createWithMessage("Failed to generate QR code. Internal error");
        }
        return result;
    }

    /**
     * Endpoint for getting QR codes based on ident and custom size. Creates squared QR codes.
     *
     * @param ident part of URL, which identifies  short link
     * @param size  number of pixels
     * @param resp  HTTP response
     * @return json with reply
     */
    @RequestMapping(method = RequestMethod.GET, value = Endpoint.Api.QR_CODE_API + "/{ident}/{size}")
    @ResponseBody
    public YalsJson getQRCodeWithCustomSize(final @PathVariable("ident") String ident,
                                            final @PathVariable("size") int size,
                                            final HttpServletResponse resp) {
        this.response = resp;

        YalsJson sizeTestResult = testSize(size);
        if (sizeTestResult instanceof YalsErrorJson) {
            return sizeTestResult;
        }

        YalsJson identTestResult = testIdentExist(ident);
        if (identTestResult instanceof YalsErrorJson) {
            return identTestResult;
        }

        Optional<String> qrCode = getQRCode(ident, size);
        YalsJson result;
        if (qrCode.isPresent()) {
            resp.setStatus(HttpCode.STATUS_200);
            result = QRCodeResponseJson.withQRCode(qrCode.get());
        } else {
            resp.setStatus(HttpCode.STATUS_500);
            result = YalsErrorJson.createWithMessage("Failed to generate QR code. Internal error");
        }
        return result;
    }

    private YalsJson testIdentExist(final String ident) {
        GetResult identFromDatabase = linkService.getLink(ident);

        if (identFromDatabase instanceof GetResult.NotFound) {
            log.debug("{} 0 idents found by request: {}", TAG, ident);
            response.setStatus(HttpCode.STATUS_404);
            return YalsErrorJson.createWithMessage("No links found by this request. "
                    + "Ident should be stored before requesting QR code").andStatus(HttpCode.STATUS_404);
        } else if (identFromDatabase instanceof GetResult.Fail) {
            log.debug("{} Failed to query DB for ident. Error: {}",
                    TAG, ((GetResult.Fail) identFromDatabase).getErrorMessage());
            response.setStatus(HttpCode.STATUS_500);
            return YalsErrorJson.createWithMessage("Server Error: Something wrong at our side");
        } else if (identFromDatabase instanceof GetResult.DatabaseDown) {
            log.debug("DB is DOWN");
            response.setStatus(HttpCode.STATUS_503);
            return YalsErrorJson.createWithMessage("Server is unreachable. Please repeat request later")
                    .andStatus(HttpCode.STATUS_503);
        } else {
            return EmptyJson.create();
        }
    }

    private YalsJson testSize(final int sizeToTest) {
        if (sizeToTest <= 0) {
            log.error("{} invalid size {}. Replying 400", TAG, sizeToTest);
            response.setStatus(HttpCode.STATUS_400);
            return YalsErrorJson.createWithMessage("Size must be positive number").andStatus(HttpCode.STATUS_400);
        } else {
            return EmptyJson.create();
        }
    }

    private Optional<String> getQRCode(final String ident, final int size) {
        try {
            return Optional.of(qrCodeService.getQRCodeFromIdent(ident, size));
        } catch (WriterException | IOException e) {
            log.error("{} Failed to generate QR code", TAG, e);
            return Optional.empty();
        }
    }
}
