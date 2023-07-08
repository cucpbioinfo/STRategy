package th.ac.chula.fims.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import th.ac.chula.fims.payload.response.MessageResponse;

import java.io.IOException;
import java.nio.charset.MalformedInputException;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = {MalformedInputException.class, IOException.class})
    public ResponseEntity<?> handleMalformedInputException(Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageResponse("Bad format file."));
    }

    @ExceptionHandler(value = {BadFormatException.class})
    public ResponseEntity<?> handleBadFormatException(Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new MessageResponse(ex.getMessage()));
    }


    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<?> handleNotFoundResource(Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
    }
}
