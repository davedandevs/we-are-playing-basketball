package online.rabko.basketball.exception;

import online.rabko.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles BadCredentialsException and returns a 401 Unauthorized response.
     *
     * @return 401 Unauthorized response
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleBadCredentials() {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    /**
     * Handles UserAlreadyExistsException and returns a 409 Conflict response.
     *
     * @return 409 Conflict response
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Error> handleUserExists(UserAlreadyExistsException exception) {
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 Bad Request response.
     *
     * @return 400 Bad Request response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .findFirst()
            .orElse("Invalid input");
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles any other exception and returns a 500 Internal Server Error response.
     *
     * @return 500 Internal Server Error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneric() {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private ResponseEntity<Error> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new Error(message));
    }
}
