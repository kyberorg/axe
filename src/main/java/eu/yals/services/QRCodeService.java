package eu.yals.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import eu.yals.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Service which generates QR codes
 *
 * @since 2.6
 */
@Slf4j
@Service
public class QRCodeService {
    public static final int DEFAULT_SIZE = 350;
    private final AppUtils appUtils;

    public QRCodeService(AppUtils appUtils) {
        this.appUtils = appUtils;
    }

    /**
     * Produces PNG with QR code, where encoded short link
     *
     * @param ident string with ident, which will added to server url
     * @return base64 encoded png with data:image/png stamp with default size {@link #DEFAULT_SIZE}
     */
    public String getQRCodeFromIdent(String ident) throws IOException, WriterException {
        return getQRCodeFromIdent(ident, DEFAULT_SIZE);
    }

    /**
     * Produces PNG with QR code, where encoded short link
     *
     * @param ident string with ident, which will added to server url
     * @param size  QR code size
     * @return ready base64 encoded png with data:image/png stamp
     */
    public String getQRCodeFromIdent(String ident, int size) throws WriterException, IOException {
        String serverUrl = appUtils.getServerUrl();
        String fullLink = serverUrl + "/" + ident;

        byte[] qrCode = doQRCode(fullLink, size);
        String base64encodedQRCode = encodeQRCode(qrCode);
        return doPng(base64encodedQRCode);
    }

    private byte[] doQRCode(String text, int size) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }

    private String encodeQRCode(byte[] qrCode) {
        final String base64Marker = "base64";
        Base64.Encoder encoder = Base64.getEncoder();
        return base64Marker + "," + encoder.encodeToString(qrCode);
    }

    private String doPng(String base64encodedQRCode) {
        final String pngMarker = "data:image/png";
        return pngMarker + ";" + base64encodedQRCode;
    }
}
