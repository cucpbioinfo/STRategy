package th.ac.chula.fims.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadFormatFileException extends RuntimeException {
    public BadFormatFileException() {
        super("Bad file format");
    }
}
