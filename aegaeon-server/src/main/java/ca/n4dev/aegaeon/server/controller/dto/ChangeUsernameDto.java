package ca.n4dev.aegaeon.server.controller.dto;

/**
 * ChangeUsernameDto.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Jun 14 - 2018
 */
public class ChangeUsernameDto {

    private String newUsername;

    private String newUsernameConfirm;

    /**
     * @return the newUsername
     */
    public String getNewUsername() {
        return newUsername;
    }

    /**
     * @param pNewUsername the newUsername to set
     */
    public void setNewUsername(String pNewUsername) {
        newUsername = pNewUsername;
    }

    /**
     * @return the newUsernameConfirm
     */
    public String getNewUsernameConfirm() {
        return newUsernameConfirm;
    }

    /**
     * @param pNewUsernameConfirm the newUsernameConfirm to set
     */
    public void setNewUsernameConfirm(String pNewUsernameConfirm) {
        newUsernameConfirm = pNewUsernameConfirm;
    }
}
