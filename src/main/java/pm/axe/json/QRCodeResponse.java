package pm.axe.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Data;

/**
 * QR Code Endpoint outgoing JSON.
 *
 * @since 2.6
 */
@Data(staticConstructor = "withQRCode")
public class QRCodeResponse implements AxeJson {
    @JsonProperty("qr_code")
    private final String qrCode;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
