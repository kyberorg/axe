package ee.yals.json;

import com.google.gson.annotations.SerializedName;
import ee.yals.json.internal.Json;

/**
 * Login Endpoint outcoming JSON
 *
 * @since 3.0
 */
public class LoginResponseJson extends Json {
    @SerializedName("token")
    private String token;

    private LoginResponseJson(String token) {
        this.token = token;
    }

    public static LoginResponseJson createWithToken(String token) {
        return new LoginResponseJson(token);
    }

    public String getToken() {
        return token;
    }
}
