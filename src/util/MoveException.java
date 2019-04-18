package util;

/**
 * An exception for when a bad move is made.
 *
 * @author Brock Dyer.
 */
public class MoveException extends Exception {

    /**
     * Construct a new MoveException with the given message.
     *
     * @param message a description of the exception.
     */
    public MoveException(String message) {
        super(message);
    }
}
