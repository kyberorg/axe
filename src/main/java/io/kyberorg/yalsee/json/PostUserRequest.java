package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Since;
import io.kyberorg.yalsee.api.user.PostUserRestController;
import io.kyberorg.yalsee.result.OperationResult;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Incoming JSON Structure for {@link PostUserRestController}.
 * {@link #email} and {@link #password} are required, {@link #username} and {@link #tfaEnabled} - optional.
 * if {@link #username} is absent - API will generate its own.
 */
@Data(staticConstructor = "create")
@Since(2.0)
public class PostUserRequest implements YalseeJson {
    private static final String ERR_NO_PASSWORD = "Password must be present";
    public static final String ERR_ANY_SHOULD_PRESENT = "Either username or email should present";
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

    /**
     * Simple {@link PostUserRequest} validator.
     *
     * @return password present -> valid, password absent -> not valid,
     * email+username present -> valid, just email or just username present - valid ,
     * both email nor username absent - not valid.
     */
    public OperationResult isValid() {
        final boolean isEmailPresent = StringUtils.isNotBlank(email);
        final boolean isUsernamePresent = StringUtils.isNotBlank(username);
        final boolean isPasswordAbsent = StringUtils.isBlank(password);

        if (isPasswordAbsent) return OperationResult.elementNotFound().withMessage(ERR_NO_PASSWORD);
        if (isEmailPresent) return OperationResult.success();
        return isUsernamePresent ? OperationResult.success() : OperationResult.elementNotFound()
                .withMessage(ERR_ANY_SHOULD_PRESENT);
    }
}
