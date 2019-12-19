package eu.yals.exception;

import eu.yals.exception.error.YalsError;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class YalsException extends RuntimeException {

    @Getter
    private String messageToUser;

    //fields for reporting
    private String id;
    private String timeStamp = new Date().toString();
    private int httpStatus;
    private String techMessage;
    private Throwable rawException;

    public YalsException() {
    }

    public YalsException(String message) {
        super(message);
    }

    /**
     * This constructor is mainly for reporting exception
     *
     * @param yalsError stored {@link YalsError} object
     */
    public YalsException(YalsError yalsError) {
        if (yalsError != null) {
            this.id = yalsError.getId();
            this.timeStamp = yalsError.getTimeStamp();
            this.httpStatus = yalsError.getHttpStatus();
            this.messageToUser = yalsError.getMessageToUser();
            this.techMessage = yalsError.getTechMessage();
            this.rawException = yalsError.getRawException();
        }
    }

    public YalsException withMessageToUser(String userMessage) {
        this.messageToUser = userMessage;
        return this;
    }

    public boolean hasMessageToUser() {
        return StringUtils.isNotBlank(messageToUser);
    }
}
