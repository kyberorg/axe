package pm.axe.test.app;

import kong.unirest.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import pm.axe.Axe;
import pm.axe.Endpoint;
import pm.axe.json.PostLinkRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static pm.axe.Axe.Headers.CONTENT_TYPE;

/**
 * Testing locations related to QR codes.
 *
 * @since 2.6
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Slf4j
public class GetQRApiTest extends UnirestTest {
    public static final String TAG = "[" + GetQRApiTest.class.getSimpleName() + "]";

    /**
     * For saving testing time, but saving long link only once.
     */
    private String ident;

    /**
     * Request for valid ident = Reply with Valid PNG QR Code.
     */
    @Test
    public void onRequestQRCodeForValidIdentAppGivesValidPngQRCode() {
        final String ident = getValidIdent();

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.OK, result.getStatus());

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

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.OK, result.getStatus());

        JsonNode body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.getObject());

        String qrCode = body.getObject().getString("qr_code");
        assertValidQRCode(qrCode);
        assertQRCodeHasExactSize(qrCode, Axe.QR.DEFAULT_QR_CODE_SIZE);
    }

    /**
     * Request QR Code for ident that not exists = 404.
     */
    @Test
    public void onRequestQRCodeWithNonExistingIdentAppGives404() {
        final String ident = "NotReallyValidIdent";
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    /**
     * Request with only numbers = 404.
     */
    @Test
    public void onRequestQRCodeWithOnlyNumbersAppGives404() {
        final int ident = 1234;
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    /**
     * Request without ident = 404.
     */
    @Test
    public void onRequestQRCodeWithNullAppGives404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + null);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
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
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.OK, result.getStatus());

        JsonNode body = result.getBody();
        String qrCode = body.getObject().getString("qr_code");

        assertValidQRCode(qrCode);
        assertQRCodeHasExactSize(qrCode, size);
    }

    /**
     * Request for valid ident and negative size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndNegativeSizeAppGives400() {
        final String ident = getValidIdent();
        final int size = -1;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and too small size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndTooSmallSizeAppGives400() {
        final String ident = getValidIdent();
        final int size = 22;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and float size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndFloatSizeAppGives400() {
        final String ident = getValidIdent();
        final double size = 44.33;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and zero size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndZeroSizeAppGives400() {
        final String ident = getValidIdent();
        final int size = 0;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and string (NaN) size = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndStringSizeAppGives400() {
        final String ident = getValidIdent();
        final String size = "size";

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + size);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request with multilevel path = 404.
     */
    @Test
    public void onRequestQRCodeToMultiLevelRequestAppGives404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/void/void2/dd/d");
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    /**
     * Request for valid ident and positive width but string (NaN) height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndWidthAndStringHeightAppGives400() {
        final String ident = getValidIdent();
        final int width = 10;
        final String height = "height";

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and negative width and negative height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndNegativeWidthAndHeightAppGives400() {
        final String ident = getValidIdent();
        final int width = -40;
        final int height = -55;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and negative width and negative height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentPositiveWidthAndNegativeHeightAppGives400() {
        final String ident = getValidIdent();
        final int width = 40;
        final int height = -35;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and zero width and height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndZeroWidthAndHeightAppGives400() {
        final String ident = getValidIdent();
        final int width = 0;
        final int height = 0;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and zero width and positive height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndZeroWidthAndPositiveHeightAppGives400() {
        final String ident = getValidIdent();
        final int width = 0;
        final int height = 35;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and positive width and zero height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndPositiveWidthAndZeroHeightAppGives400() {
        final String ident = getValidIdent();
        final int width = 40;
        final int height = 0;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and floating width and height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndFloatWidthAndHeightAppGives400() {
        final String ident = getValidIdent();
        final double width = 46.53;
        final double height = 35.36;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and too small width and height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndTooSmallWidthAndHeightAppGives400() {
        final String ident = getValidIdent();
        final double width = 10;
        final double height = 25;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and normal width and to small height = 400.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndNormalWidthAndTooSmallHeightAppGives400() {
        final String ident = getValidIdent();
        final double width = 34;
        final double height = 20;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    /**
     * Request for valid ident and positive width and positive height = 200 and QR Code with correct size.
     */
    @Test
    public void onRequestQRCodeWithValidIdentAndPositiveWidthAndHeightAppGives200AndQRCodeWithCorrectSizes()
            throws IOException {
        final String ident = getValidIdent();
        final int width = 40;
        final int height = 35;

        HttpRequest request =
                Unirest.get(TEST_URL + Endpoint.Api.QR_API + "/" + ident + "/" + width + "/" + height);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(HttpStatus.OK, result.getStatus());

        JsonNode body = result.getBody();
        String qrCode = body.getObject().getString("qr_code");

        assertValidQRCode(qrCode);
        assertQRCodeHasExactWidthAndHeight(qrCode, width, height);
    }

    private String getValidIdent() {
        if (this.ident == null) {
            final String longUrlToSave = "https://github.com/kyberorg/axe/issues";
            PostLinkRequest storeRequest = PostLinkRequest.create().withLink(longUrlToSave);

            HttpRequest request =
                    Unirest.post(TEST_URL + Endpoint.Api.LINKS_API)
                            .header(CONTENT_TYPE, MimeTypes.JSON)
                            .body(storeRequest.toString());
            HttpResponse<JsonNode> result = request.asJson();

            logRequestAndResponse(request, result, TAG);

            if (result.getStatus() != HttpStatus.CREATED) {
                log.error("Store API fail");
                throw new IllegalStateException("Could not get short link from Store API");
            }

            JsonNode body = result.getBody();
            ident = body.getObject().getString("ident");
        }
        return this.ident;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void assertValidQRCode(final String qrCode) {
        assertTrue(StringUtils.isNotBlank(qrCode), "QR find, but cannot be empty");

        String[] qrCodeParts = qrCode.split(";");
        if (qrCodeParts.length > 1) {
            // base64,iVBORw0KGgoAAAAN...
            String[] valueParts = qrCodeParts[1].split(",");
            if (valueParts.length > 1) {
                assertEquals("base64", valueParts[0], "QR code must be encoded with base64");
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

    private void assertQRCodeHasExactSize(final String qrCode, final int size) throws IOException {
        assertQRCodeHasExactWidthAndHeight(qrCode, size, size);
    }

    private void assertQRCodeHasExactWidthAndHeight(final String qrCode, final int width, final int height)
            throws IOException {
        String imageString = qrCode.split(",")[1];
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedImage = decoder.decode(imageString);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedImage);
        BufferedImage image = ImageIO.read(bis);
        bis.close();

        assertEquals(width, image.getWidth(), "Width is wrong");
        assertEquals(height, image.getHeight(), "Height is wrong");
    }
}
