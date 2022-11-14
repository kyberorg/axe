package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Since;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data(staticConstructor = "create")
@Since(2.0)
public class PostUserRequest implements YalseeJson {
    @NotNull(message = "Email must be present")
    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @ToString.Exclude
    @NotNull(message = "Password must be present")
    @JsonProperty("password")
    private String password;

    @JsonProperty("tfa_enabled")
    private boolean tfaEnabled = false;
}
