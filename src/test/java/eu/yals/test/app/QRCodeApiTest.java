package eu.yals.test.app;

import eu.yals.Endpoint;
import eu.yals.constants.Header;
import eu.yals.constants.MimeType;
import eu.yals.json.StoreRequestJson;
import eu.yals.services.QRCodeService;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import static org.junit.Assert.*;

/**
 * Testing locations related to QR codes
 *
 * @since 2.6
 */
@SuppressWarnings("unchecked")
@Slf4j
public class QRCodeApiTest extends UnirestTest {

    @Test
    public void onRequestQRCodeForValidIdentAppGivesValidPngQRCode() {
        final String ident = getValidIdent();

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(200, result.getStatus());

        JsonNode body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.getObject());

        String qrCode = body.getObject().getString("qrCode");
        assertValidQRCode(qrCode);
    }

    @Test
    public void onRequestQRCodeForValidIdentAppGivesValidPngQRCodeWithDefaultSize() throws Exception {
        final String ident = getValidIdent();

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(200, result.getStatus());

        JsonNode body = result.getBody();
        assertNotNull(body);
        assertNotNull(body.getObject());

        String qrCode = body.getObject().getString("qrCode");
        assertValidQRCode(qrCode);
        assertQRCodeHasExactSize(QRCodeService.DEFAULT_SIZE, qrCode);
    }

    @Test
    public void onRequestQRCodeWithNonExistingIdentAppGives404() {
        final String ident = "NotReallyValidIdent";
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());
    }

    @Test
    public void onRequestQRCodeWithOnlyNumbersAppGives404() {
        final int ident = 1234;
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());
    }

    @Test
    public void onRequestQRCodeWithNullAppGives404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + null)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());
    }

    @Test
    public void onRequestQRCodeWithValidIdentAndSizeAppGivesQRCodeWithRequestedSize() throws Exception {
        final String ident = getValidIdent();
        final int size = 100;

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident + "/" + size)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(200, result.getStatus());

        JsonNode body = result.getBody();
        String qrCode = body.getObject().getString("qrCode");

        assertValidQRCode(qrCode);
        assertQRCodeHasExactSize(size, qrCode);
    }

    @Test
    public void onRequestQRCodeWithValidIdentAndNegativeSizeAppGives400() {
        final String ident = "IdentOne";
        final int size = -1;

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident + "/" + size)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(400, result.getStatus());
    }

    @Test
    public void onRequestQRCodeWithValidIdentAndZeroSizeAppGives400() {
        final String ident = "IdentOne";
        final int size = 0;

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident + "/" + size)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);


        assertEquals(400, result.getStatus());
    }

    @Test
    public void onRequestQRCodeWithValidIdentAndStringSizeAppGives400() {
        final String ident = "IdentOne";
        final String size = "size";

        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + ident + "/" + size)
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(400, result.getStatus());
    }

    @Test
    public void onRequestQRCodeToMultiLevelRequestAppGives404() {
        HttpRequest request = Unirest.get(TEST_URL + Endpoint.QR_CODE_API_BASE + "/void/void2/dd")
                .header(Header.CONTENT_TYPE, MimeType.APPLICATION_JSON);
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        assertEquals(404, result.getStatus());
    }

    private String getValidIdent() {
        final String longUrlToSave = "https://github.com/yadevee/yals/issues";
        StoreRequestJson storeRequest = StoreRequestJson.create().withLink(longUrlToSave);

        HttpRequest request = Unirest.post(TEST_URL + Endpoint.STORE_API).body(storeRequest.toString());
        HttpResponse<JsonNode> result = request.asJson();

        logRequestAndResponse(request, result, TAG);

        if (result.getStatus() != 201) {
            log.error("Store API fail");
            throw new RuntimeException("Could not get short link from Store API");
        }

        JsonNode body = result.getBody();
        return body.getObject().getString("ident");
    }

    private void assertValidQRCode(String qrCode) {
        assertTrue("QR find, but cannot be empty", StringUtils.isNotBlank(qrCode));

        String[] qrCodeParts = qrCode.split(";");
        if (qrCodeParts.length > 1) {
            //base64,iVBORw0KGgoAAAAN...
            String[] valueParts = qrCodeParts[1].split(",");
            if (valueParts.length > 1) {
                assertEquals("QR code must be encoded with base64", "base64", valueParts[0]);
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

        //QR code is square
        assertEquals("Width is wrong", size, image.getWidth());
        assertEquals("Height is wrong", size, image.getHeight());
    }
}
