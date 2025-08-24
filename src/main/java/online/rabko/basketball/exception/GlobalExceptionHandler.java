package online.rabko.basketball.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import online.rabko.model.Error;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for translating application and framework exceptions into standardized
 * HTTP responses with {@link online.rabko.model.Error} bodies.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles invalid authentication credentials. Returns 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    /**
     * Handles attempts to access resources without sufficient permissions. Returns 403 Forbidden.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Error> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "Access is denied");
    }

    /**
     * Handles conflicts when creating a user that already exists. Returns 409 Conflict.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Error> handleUserExists(UserAlreadyExistsException ex) {
        log.warn("User conflict: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles entity existence conflicts (e.g. duplicate keys). Returns 409 Conflict.
     */
    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Error> handleEntityExists(EntityExistsException ex) {
        log.warn("Entity exists: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles cases when the requested entity or resource is not found. Returns 404 Not Found.
     */
    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<Error> handleNotFound(RuntimeException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles errors triggered by {@code @Valid}. Returns 400 Bad Request with the
     * first field error message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValidValidation(
        MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .findFirst().orElse("Invalid input");
        log.warn("Bean validation: {}", msg);
        return build(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * Handles constraint violations thrown by the validation framework. Returns 400 Bad Request
     * with the first violation message.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Error> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .findFirst().orElse("Invalid input");
        log.warn("Constraint violation: {}", msg);
        return build(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * Handles validation errors aggregated by Spring Data REST. Returns 400 Bad Request with the
     * first error message.
     */
    @ExceptionHandler(RepositoryConstraintViolationException.class)
    public ResponseEntity<Error> handleRepositoryConstraint(
        RepositoryConstraintViolationException ex) {
        String msg = ex.getErrors().getAllErrors().stream()
            .map(e -> e.getDefaultMessage())
            .findFirst().orElse("Invalid input");
        log.warn("Repository constraint: {}", msg);
        return build(HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * Handles malformed request parameters, type mismatches, and unreadable bodies. Returns 400 Bad
     * Request.
     */
    @ExceptionHandler({
        MethodArgumentTypeMismatchException.class,
        ConversionFailedException.class,
        MissingServletRequestParameterException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<Error> handleRequestFormat(Exception ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Malformed request");
    }

    /**
     * Handles database constraint violations (e.g. unique, foreign key). Returns 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Error> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);
        return build(HttpStatus.CONFLICT, "Data integrity violation");
    }

    /**
     * Handles transaction failures and unwraps validation errors inside transactions. Returns 400
     * Bad Request if caused by validation, otherwise 500 Internal Server Error.
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Error> handleTx(TransactionSystemException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof ConstraintViolationException cve) {
            return handleConstraintViolation(cve);
        }
        log.error("Transaction error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction failed");
    }

    /**
     * Handles unsupported HTTP methods. Returns 405 Method Not Allowed.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Error> handleMethodNotSupported(
        HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed");
    }

    /**
     * Handles unsupported content types. Returns 415 Unsupported Media Type.
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Error> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type");
    }

    /**
     * Handles all unexpected exceptions not covered by other handlers. Returns 500 Internal Server
     * Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    /**
     * Builds a standardized {@link Error} response with the given status and message.
     */
    private ResponseEntity<Error> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new Error(message));
    }
}
