package eu.yals.exception.error;

import org.apache.commons.lang3.StringUtils;

public class YalsErrorBuilder {
    private String messageToUser;
    private String techMessage;
    private int httpStatus = 500;
    private Throwable rawException = null;

    public static YalsErrorBuilder withTechMessage(String techMessage) {
        return new YalsErrorBuilder(techMessage);
    }
    
    private YalsErrorBuilder(String techMessage) {
        this.techMessage = techMessage;
    }

    public YalsErrorBuilder withMessageToUser(String messageToUser) {
        this.messageToUser = messageToUser;
        return this;
    }

    public YalsErrorBuilder withStatus(int status) {
        this.httpStatus = status;
        return this;
    }

    public YalsErrorBuilder addRawException(Throwable throwable) {
        this.rawException = throwable;
        return this;
    }

    public YalsError build() {
        YalsError yalsError = new YalsError();
        if (StringUtils.isNotBlank(techMessage)) {
            yalsError.setTechMessage(techMessage);
        }

        if (StringUtils.isNotBlank(messageToUser)) {
            yalsError.setMessageToUser(messageToUser);
        }
        yalsError.setHttpStatus(httpStatus);

        if (rawException != null) {
            yalsError.setRawException(rawException);
        }

        return yalsError;
    }
}
