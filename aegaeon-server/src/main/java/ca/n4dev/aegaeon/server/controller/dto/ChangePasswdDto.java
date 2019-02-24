package ca.n4dev.aegaeon.server.controller.dto;

/**
 * ChangePasswdDto.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Jun 10 - 2018
 */
public class ChangePasswdDto {

    private String oldPassword;

    private String newPassword;

    private String newPasswordConfirm;

    /**
     * @return the oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * @param pOldPassword the oldPassword to set
     */
    public void setOldPassword(String pOldPassword) {
        oldPassword = pOldPassword;
    }

    /**
     * @return the newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @param pNewPassword the newPassword to set
     */
    public void setNewPassword(String pNewPassword) {
        newPassword = pNewPassword;
    }

    /**
     * @return the newPasswordConfirm
     */
    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    /**
     * @param pNewPasswordConfirm the newPasswordConfirm to set
     */
    public void setNewPasswordConfirm(String pNewPasswordConfirm) {
        newPasswordConfirm = pNewPasswordConfirm;
    }
}
