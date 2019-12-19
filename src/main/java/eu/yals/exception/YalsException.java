package eu.yals.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;


@EqualsAndHashCode(callSuper = true)
@Data
public class YalsException extends RuntimeException {

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
