package ca.n4dev.aegaeon.api.exception;

/**
 * DuplicateUsernameException.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - May 17 - 2019
 */
public class DuplicateUsernameException extends BaseException {

    private final String currentUsername;
    private final String nextUsername;

    public DuplicateUsernameException(String pCurrentUsername, String pNextUsername) {
        super(String.format("The username [%s] is already taken and cannot be used by [%]",
                            pNextUsername, pCurrentUsername));
        currentUsername = pCurrentUsername;
        nextUsername = pNextUsername;
    }

    /**
     * @return the currentUsername
     */
    public String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * @return the nextUsername
     */
    public String getNextUsername() {
        return nextUsername;
    }
}
