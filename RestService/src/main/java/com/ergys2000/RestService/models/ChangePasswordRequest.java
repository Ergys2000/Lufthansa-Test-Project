package com.ergys2000.RestService.models;

/** Defines the structure of a change password request */
public class ChangePasswordRequest {
	private String oldPassword;
    private String newPassword;
	private String confirmPassword;

	public ChangePasswordRequest() {
	}
	public String getOldPassword() {
        return oldPassword;
    }
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
