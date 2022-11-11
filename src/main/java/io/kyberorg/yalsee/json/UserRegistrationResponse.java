package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.Since;


@Since(2.0)
public class UserRegistrationResponse {
    public static final String MESSAGE_TEMPLATE = "User successfully registered. Confirmation sent to ";

    @JsonProperty("message")
    private String message;

    public static UserRegistrationResponse create(final String email) {
        UserRegistrationResponse response = new UserRegistrationResponse();
        response.message = MESSAGE_TEMPLATE + email;
        return response;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
