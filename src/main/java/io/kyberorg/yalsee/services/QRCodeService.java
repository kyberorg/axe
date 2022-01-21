package io.kyberorg.yalsee.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.kyberorg.yalsee.constants.App;
import io.kyberorg.yalsee.core.IdentValidator;
import io.kyberorg.yalsee.result.OperationResult;
import io.kyberorg.yalsee.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Service which generates QR codes.
 *
 * @since 2.6
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class QRCodeService {
    private static final String TAG = "[" + QRCodeService.class.getSimpleName() + "]";

    public static final String ERR_MALFORMED_IDENT = "Ident is not valid";
    public static final String ERR_MALFORMED_SIZE = "Size is too small. Must be " + App.QR.MINIMAL_SIZE_IN_PIXELS
            + "px or more";
    public static final String ERR_MALFORMED_WIDTH = "Width is too small. Must be " + App.QR.MINIMAL_SIZE_IN_PIXELS
            + "px or more";
    public static final String ERR_MALFORMED_HEIGHT = "Height is too small. Must be " + App.QR.MINIMAL_SIZE_IN_PIXELS
            + "px or more";
    public static final String ERR_QR_CREATE_IO_EXCEPTION = "Failed to create QR code: I/O exception";

    private final AppUtils appUtils;
    private final IdentValidator identValidator;
    private final LinkService linkService;

    /**
     * Produces base64 encoded PNG with QR code with encoded short link and {@link App.QR#DEFAULT_QR_CODE_SIZE}.
     *
     * @param ident string with ident, which will added to short url
     * @return same as {{@link #getQRCode(String, int, int)}},
     */
    public OperationResult getQRCode(final String ident) {
        return getQRCode(ident, App.QR.DEFAULT_QR_CODE_SIZE, App.QR.DEFAULT_QR_CODE_SIZE);
    }

    /**
     * Produces base64 encoded PNG with squared QR code, where encoded short link, with given size.
     *
     * @param ident string with ident, which will added to short url
     * @param size positive integer with QR code size. Should be {@link App.QR#MINIMAL_SIZE_IN_PIXELS} px or more
     * @return same as {{@link #getQRCode(String, int, int)}},
     * plus {@link OperationResult#MALFORMED_INPUT} with {@link #ERR_MALFORMED_SIZE} message
     */
    public OperationResult getQRCode(final String ident, final int size) {
        if (size <= 0) {
            log.error("{} Request has negative size: {}", TAG, size);
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_SIZE);
        }
        return getQRCode(ident, size, size);
    }

    /**
     * Produces base64 encoded PNG with QR code, where encoded short link, with given width and height.
     *
     * @param ident string with ident, which will added to short url
     * @param width positive integer with QR code width
     * @param height positive integer with QR code height
     * @return {@link OperationResult} with:
     *
     * {@link OperationResult#OK} and {@link String} with base64 encoded PNG with QR code
     * in {@link OperationResult#payload}.
     *
     * {@link OperationResult#MALFORMED_INPUT} with {@link OperationResult#message}:
     * {@link #ERR_MALFORMED_IDENT}, {@link #ERR_MALFORMED_WIDTH}, {@link #ERR_MALFORMED_HEIGHT}
     * when ident, width or height is malformed, negative or less then {@link App.QR#MINIMAL_SIZE_IN_PIXELS}.
     * {@link OperationResult#ELEMENT_NOT_FOUND} when nothing stored under given ident
     * {@link OperationResult#SYSTEM_DOWN} when system or its parts unreachable
     * {@link OperationResult#GENERAL_FAIL} with {@link OperationResult#message} when something unexpected happened.
     */
    public OperationResult getQRCode(final String ident, final int width, final int height) {
        //input check
        OperationResult validateParamsResult = checkInputs(ident, width, height);
        if (validateParamsResult.notOk()) {
            return validateParamsResult;
        }

        //searching for ident
        OperationResult identSearchResult = linkService.isLinkWithIdentExist(ident);
        if (identSearchResult.notOk()) {
            return identSearchResult;
        }

        //action
        String shortUrl = appUtils.getShortUrl();
        String fullLink = shortUrl + "/" + ident;

        byte[] qrCode;
        try {
            qrCode = doQRCode(fullLink, width, height);
        } catch (WriterException | IOException e) {
            log.error("{} failed to create QR code: {}", TAG, e.getMessage());
            return OperationResult.generalFail().withMessage(ERR_QR_CREATE_IO_EXCEPTION);
        }

        String base64encodedQRCode = encodeQRCode(qrCode);
        String png = doPng(base64encodedQRCode);

        return OperationResult.success().addPayload(png);
    }

    private byte[] doQRCode(final String text, final int width, final int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    private String encodeQRCode(final byte[] qrCode) {
        final String base64Marker = "base64";
        Base64.Encoder encoder = Base64.getEncoder();
        return base64Marker + "," + encoder.encodeToString(qrCode);
    }

    private String doPng(final String base64encodedQRCode) {
        final String pngMarker = "data:image/png";
        return pngMarker + ";" + base64encodedQRCode;
    }

    private OperationResult checkInputs(final String ident, final int width, final int height) {
        //ident check
        OperationResult identCheck = identValidator.validate(ident);
        if (identCheck.notOk()) {
            log.error("{} Request has not valid ident: {}", TAG, ident);
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_IDENT);
        }

        //width check
        if (width < App.QR.MINIMAL_SIZE_IN_PIXELS) {
            log.error("{} Request has negative width: {}", TAG, width);
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_WIDTH);
        }

        //height check
        if (height < App.QR.MINIMAL_SIZE_IN_PIXELS) {
            log.error("{} Request has negative height: {}", TAG, height);
            return OperationResult.malformedInput().withMessage(ERR_MALFORMED_HEIGHT);
        }
        return OperationResult.success();
    }
}
