package ca.n4dev.aegaeon.server.view;

/**
 * CreateAccountView.java
 *
 * View holding create-account form submission.
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 18 - 2018
 */
public class CreateAccountView {

    private String username;

    private String password;

    private String passwordConfirm;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param pUsername the username to set
     */
    public void setUsername(String pUsername) {
        username = pUsername;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param pPassword the password to set
     */
    public void setPassword(String pPassword) {
        password = pPassword;
    }

    /**
     * @return the passwordConfirm
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * @param pPasswordConfirm the passwordConfirm to set
     */
    public void setPasswordConfirm(String pPasswordConfirm) {
        passwordConfirm = pPasswordConfirm;
    }
}
