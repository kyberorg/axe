package eu.yals.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public class YalsException extends RuntimeException {

    @Getter
    private String messageToUser;

    public YalsException() {
    }

    public YalsException(String message) {
        super(message);
    }

    public YalsException withMessageToUser(String userMessage) {
        this.messageToUser = userMessage;
        return this;
    }

    public boolean hasMessageToUser() {
        return StringUtils.isNotBlank(messageToUser);
    }
}
