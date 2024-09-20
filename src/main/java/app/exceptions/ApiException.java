package app.exceptions;

import ch.qos.logback.classic.Logger;
import lombok.Getter;
import org.slf4j.LoggerFactory;

/**
 *  Thrown when an exception occurs in the API layer.
 */
@Getter
public class ApiException extends RuntimeException {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(ApiException.class);

    public ApiException(String message) {
        super(message);
        writeToLog(message);
    }

    private void writeToLog(String message) {
        logger.error(message);
    }
}
