package online.rabko.basketball.exception;

/**
 * Exception to be thrown when a user with the same username already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified username.
     *
     * @param username the username of the user that already exists
     */
    public UserAlreadyExistsException(String username) {
        super("User with username '" + username + "' already exists");
    }
}
