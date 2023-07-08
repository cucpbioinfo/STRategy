package th.ac.chula.fims.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String resource, int id) {
        super(String.format("%s ID: %d not found.", resource, id));
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s ID: %d not found.", resource, id));
    }

    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s ID: %s not found.", resource, id));
    }

    public ResourceNotFoundException(String resource, String field, String value) {
        super(String.format("%s %s: %s not found.", resource, field, value));
    }

    public ResourceNotFoundException(String customMessage) {
        super(customMessage);
    }
}