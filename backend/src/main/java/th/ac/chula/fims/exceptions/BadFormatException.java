package th.ac.chula.fims.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadFormatException extends RuntimeException {
    public BadFormatException() {
        super("Bad format entity");
    }

    public BadFormatException(String message) {
        super(message);
    }
}
