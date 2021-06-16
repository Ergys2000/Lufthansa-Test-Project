package com.ergys2000.RestService.models;


public class ResetPasswordRequest {
	private String password;

	public ResetPasswordRequest() {

	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
