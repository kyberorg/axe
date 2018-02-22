package ee.yals.json;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import ee.yals.json.internal.Json;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Login Endpoint incoming JSON
 *
 * @since 3.0
 */
public class LoginRequestJson extends Json {
    @NotNull(message = "must be present")
    @Size(min = 2, max = 256)
    @Since(3.0)
    @SerializedName("yals_username")
    private String username;

    @NotNull(message = "must be present")
    @Size(min = 2, max = 256)
    @Since(1.0)
    @SerializedName("yals_password")
    private String plainPass;

    public static LoginRequestJson createWithUsername(String username) {
        LoginRequestJson newInstance = new LoginRequestJson();
        newInstance.username = StringUtils.isNotBlank(username) ? username : null;
        return newInstance;
    }

    public LoginRequestJson andPassword(String password) {
        this.plainPass = StringUtils.isNotBlank(password) ? password : null;
        return this;
    }


    public String getUsername() {
        return username;
    }

    public String getPlainPass() {
        return plainPass;
    }
}
