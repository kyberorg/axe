package io.kyberorg.yalsee.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.Since;


/**
 * JSON given back to user, when User successfully registered.
 */
@Since(2.0)
public class PostUserResponse {
    public static final String MESSAGE_TEMPLATE = "User successfully registered. Confirmation sent to ";

    @JsonProperty("message")
    private String message;

    /**
     * Creates {@link PostUserResponse}.
     *
     * @param email string with email address to complete {@link #message}.
     * @return created {@link PostUserResponse}, which can be JSONized.
     */
    public static PostUserResponse create(final String email) {
        PostUserResponse response = new PostUserResponse();
        response.message = MESSAGE_TEMPLATE + email;
        return response;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
