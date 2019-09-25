package eu.yals.controllers.rest;

import com.google.zxing.WriterException;
import eu.yals.Endpoint;
import eu.yals.json.ErrorJson;
import eu.yals.json.QRCodeResponseJson;
import eu.yals.json.internal.Json;
import eu.yals.services.QRCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Generate QR from short link
 *
 * @since 2.6
 */
@Slf4j
@RestController
public class QRCodeRestController {
    private static final String TAG = "[API QR Code]";
    private QRCodeService qrCodeService;

    public QRCodeRestController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @RequestMapping(method = RequestMethod.GET, value = Endpoint.QR_CODE_API)
    @ResponseBody
    public Json getQrCode(@PathVariable("ident") String ident, HttpServletResponse response) {
        response.setStatus(200);

        String qrCode;
        try {
            qrCode = qrCodeService.getQRCodeFromIdent(ident);
        } catch (IOException | WriterException e) {
            log.error("Failed to generate QR code", e);
            response.setStatus(500);
            return ErrorJson.createWithMessage("Failed to generate QR code. Internal error");
        }
        return QRCodeResponseJson.withQrCode(qrCode);
    }
}
