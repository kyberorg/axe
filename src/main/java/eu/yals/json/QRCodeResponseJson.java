package eu.yals.json;

import com.google.gson.annotations.Since;
import eu.yals.json.internal.Json;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * QR Code Endpoint outgoing JSON
 *
 * @since 2.6
 */
@EqualsAndHashCode(callSuper = true)
@Data(staticConstructor = "withQRCode")
public class QRCodeResponseJson extends Json {
    @Since(2.6)
    private final String qrCode;
}
