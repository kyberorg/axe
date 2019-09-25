package eu.yals.json;

import com.google.gson.annotations.Since;
import eu.yals.json.internal.Json;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * QrCode Endpoint outgoing JSON
 *
 * @since 2.6
 */
@EqualsAndHashCode(callSuper = true)
@Data(staticConstructor = "withQrCode")
public class QRCodeResponseJson extends Json {
    @Since(2.6)
    private final String qrCode;
}
