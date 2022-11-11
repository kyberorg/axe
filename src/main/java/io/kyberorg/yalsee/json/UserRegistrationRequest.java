package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.Since;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data(staticConstructor = "create")
@Since(2.0)
public class UserRegistrationRequest implements YalseeJson {
    @NotNull(message = "Email must be present")
    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @NotNull(message = "Password must be present")
    @JsonProperty("password")
    private String password;

    @JsonProperty("tfa_enabled")
    private boolean tfaEnabled = false;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
