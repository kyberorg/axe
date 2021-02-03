package io.kyberorg.yalsee.test.app;

import io.kyberorg.yalsee.Endpoint;
import io.kyberorg.yalsee.json.StoreRequestJson;
import io.kyberorg.yalsee.services.QRCodeService;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static io.kyberorg.yalsee.constants.HttpCode.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing locations related to QR codes.
 *
 * @since 2.6
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class QRCodeApiTest extends UnirestTest {
    public static final String TAG = "[" + QRCodeApiTest.class.getSimpleName() + "]";

    /**
     * Request for valid ident = Reply with Valid PNG QR Code.
     */
    @Test
    public void onRequestQRCodeForValidIdentAppGivesValidPngQRCode() {
        final String ident = getValidIdent();

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_200, result.getStatus());

        JsonNode body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.getObject());

        String qrCode = body.getObject().getString("qr_code");
        assertValidQRCode(qrCode);
    }

    /**
     * Request for valid ident = Reply with Valid PNG QR Code with default size.
     *
     * @throws Exception when failed to check size
     */
    @Test
    public void onRequestQRCodeForValidIdentAppGivesValidPngQRCodeWithDefaultSize() throws Exception {
        final String ident = getValidIdent();

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_200, result.getStatus());

        JsonNode body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.getObject());

        String qrCode = body.getObject().getString("qr_code");
        assertValidQRCode(qrCode);
        assertQRCodeHasExactSize(QRCodeService.DEFAULT_SIZE, qrCode);
    }

    /**
     * Request QR Code for ident that not exists = 404.
     */
    @Test
    public void onRequestQRCodeWithNonExistingIdentAppGives404() {
        final String ident = "NotReallyValidIdent";
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());
    }

    /**
     * Request with only numbers = 404.
     */
    @Test
    public void onRequestQRCodeWithOnlyNumbersAppGives404() {
        final int ident = 1234;
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());
    }

    /**
     * Request without ident = 404.
     */
    @Test
    public void onRequestQRCodeWithNullAppGives404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + null);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());
    }

    /**
     * Request for valid ident and desired size = Reply with Valid PNG QR Code with requested size.
     *
     * @throws Exception when failed to check size
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndSizeAppGivesQRCodeWithRequestedSize()
            throws Exception {
        final String ident = getValidIdent();
        final int size = 100;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_200, result.getStatus());

        JsonNode body = result.getBody();
        String qrCode = body.getObject().getString("qr_code");

        assertValidQRCode(qrCode);
        assertQRCodeHasExactSize(size, qrCode);
    }

    /**
     * Request for valid ident and negative size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndNegativeSizeAppGives400() {
        final String ident = "IdentOne";
        final int size = -1;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_400, result.getStatus());
    }

    /**
     * Request for valid ident and zero size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndZeroSizeAppGives400() {
        final String ident = "IdentOne";
        final int size = 0;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_400, result.getStatus());
    }

    /**
     * Request for valid ident and string (NaN) size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndStringSizeAppGives400() {
        final String ident = "IdentOne";
        final String size = "size";

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_400, result.getStatus());
    }

    /**
     * Request with multilevel path = 404.
     */
    @Test
    public void onRequestQRCodeToMultiLevelRequestAppGives404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.ForTests.QR_CODE_API + "/void/void2/dd");
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(STATUS_404, result.getStatus());
    }

    private String getValidIdent() {
        final String longUrlToSave = "https://github.com/kyberorg/yalsee/issues";
        StoreRequestJson storeRequest = StoreRequestJson.create().withLink(longUrlToSave);

        HttpRequest request =
                Unirest.post(TEST_URL + Endpoint.Api.STORE_API).body(storeRequest.toString());
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        if (result.getStatus() != STATUS_201) {
            log.error("Store API fail");
            throw new IllegalStateException("Could not get short link from Store API");
        }

        JsonNode body = result.getBody();
        return body.getObject().getString("ident");
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void assertValidQRCode(final String qrCode) {
        assertTrue(StringUtils.isNotBlank(qrCode), "QR find, but cannot be empty");

        String[] qrCodeParts = qrCode.split(";");
        if (qrCodeParts.length > 1) //noinspection SpellCheckingInspection
        {
            // base64,iVBORw0KGgoAAAAN...
            String[] valueParts = qrCodeParts[1].split(",");
            if (valueParts.length > 1) {
                assertEquals( "base64", valueParts[0], "QR code must be encoded with base64");
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] decodedValue = decoder.decode(valueParts[1]);
                assertNotNull(decodedValue);
                assertTrue(decodedValue.length > 0);
            } else {
                fail("Malformed QR code: " + qrCode);
            }
        } else {
            fail("Malformed QR code: " + qrCode);
        }
    }

    private void assertQRCodeHasExactSize(int size, String qrCode) throws IOException {
        String imageString = qrCode.split(",")[1];
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedImage = decoder.decode(imageString);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedImage);
        BufferedImage image = ImageIO.read(bis);
        bis.close();

        // QR code is square
        assertEquals(size, image.getWidth(), "Width is wrong");
        assertEquals(size, image.getHeight(), "Height is wrong");
    }
}
