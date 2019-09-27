package eu.yals.controllers.rest;

import com.google.zxing.WriterException;
import eu.yals.Endpoint;
import eu.yals.json.EmptyJson;
import eu.yals.json.ErrorJson;
import eu.yals.json.QRCodeResponseJson;
import eu.yals.json.internal.Json;
import eu.yals.result.GetResult;
import eu.yals.services.LinkService;
import eu.yals.services.QRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Generates QR from short link
 *
 * @since 2.6
 */
@Slf4j
@RestController
public class QRCodeRestController {
    private static final String TAG = "[API QR Code]";
    private QRCodeService qrCodeService;
    private LinkService linkService;

    private HttpServletResponse response;

    public QRCodeRestController(QRCodeService qrCodeService, @Qualifier("dbStorage") LinkService linkService) {
        this.qrCodeService = qrCodeService;
        this.linkService = linkService;
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.QR_CODE_API)
    @ResponseBody
    public Json getQRCode(@PathVariable("ident") String ident, HttpServletResponse response) {
        this.response = response;

        Json testResult = testIdentExist(ident);
        if (testResult instanceof ErrorJson) {
            return testResult;
        }

        String qrCode;
        try {
            qrCode = qrCodeService.getQRCodeFromIdent(ident);
        } catch (IOException | WriterException e) {
            log.error("{} Failed to generate QR code", TAG, e);
            response.setStatus(500);
            return ErrorJson.createWithMessage("Failed to generate QR code. Internal error");
        }
        response.setStatus(200);
        return QRCodeResponseJson.withQRCode(qrCode);
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.CUSTOM_SIZE_QR_CODE_API)
    @ResponseBody
    public Json getQRCodeWithCustomSize(@PathVariable("ident") String ident,
                                        @PathVariable("size") int size,
                                        HttpServletResponse response) {
        this.response = response;

        //testing size
        if (size <= 0) {
            log.error("{} invalid size {}. Replying 400", TAG, size);
            response.setStatus(400);
            return ErrorJson.createWithMessage("Size must be positive number");
        }

        //testing ident
        Json testResult = testIdentExist(ident);
        if (testResult instanceof ErrorJson) {
            return testResult;
        }

        String qrCode;
        try {
            qrCode = qrCodeService.getQRCodeFromIdent(ident, size);
        } catch (IOException | WriterException e) {
            log.error("{} Failed to generate QR code", TAG, e);
            response.setStatus(500);
            return ErrorJson.createWithMessage("Failed to generate QR code. Internal error");
        }

        response.setStatus(200);
        return QRCodeResponseJson.withQRCode(qrCode);

    }

    private Json testIdentExist(String ident) {
        GetResult identFromDatabase = linkService.getLink(ident);

        if (identFromDatabase instanceof GetResult.NotFound) {
            log.debug("{} 0 idents found by request: {}", TAG, ident);
            response.setStatus(404);
            return ErrorJson.createWithMessage("No links found by this request. Ident should be stored before requesting QR code");
        } else if (identFromDatabase instanceof GetResult.Fail) {
            log.debug("{} Failed to query DB for ident. Error: {}", TAG, ((GetResult.Fail) identFromDatabase).getErrorMessage());
            response.setStatus(500);
            return ErrorJson.createWithMessage("Server Error: Something wrong at our side");
        } else if (identFromDatabase instanceof GetResult.DatabaseDown) {
            log.debug("DB is DOWN");
            response.setStatus(503);
            return ErrorJson.createWithMessage("Server is unreachable. Please repeat request later");
        } else {
            return EmptyJson.create();
        }
    }

    private String getQRCode(String ident, int size) throws IOException, WriterException {
        return qrCodeService.getQRCodeFromIdent(ident, size);
    }

    private String getQRCode(String ident) throws IOException, WriterException {
        return getQRCode(ident, QRCodeService.DEFAULT_SIZE);
    }


}
