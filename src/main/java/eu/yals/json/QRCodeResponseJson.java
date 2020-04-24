package eu.yals.json;

import eu.yals.json.internal.Json;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * QR Code Endpoint outgoing JSON.
 *
 * @since 2.6
 */
@EqualsAndHashCode(callSuper = true)
@Data(staticConstructor = "withQRCode")
public class QRCodeResponseJson extends Json {
    private final String qrCode;
}
