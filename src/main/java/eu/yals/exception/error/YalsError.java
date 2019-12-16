package eu.yals.exception.error;

import lombok.Data;

import java.util.Date;

@Data
public class YalsError {
    private String id;
    private final String timeStamp = new Date().toString();
    private String messageToUser;
    private String techMessage;
    private int httpStatus = 500;
    private Throwable rawException;
}
