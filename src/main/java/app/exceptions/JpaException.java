package app.exceptions;

import ch.qos.logback.classic.Logger;
import lombok.Getter;
import org.slf4j.LoggerFactory;

/**
 *  Thrown when an exception occurs in the JPA layer.
 */
@Getter
public class JpaException extends RuntimeException {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(JpaException.class);

    public JpaException(String message) {
        super(message);
        writeToLog(message);
    }

    private void writeToLog(String message) {
        logger.error(message);
    }
}