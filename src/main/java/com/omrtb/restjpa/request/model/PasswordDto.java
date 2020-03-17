package com.omrtb.restjpa.request.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.omrtb.restjpa.validations.ResetPasswordEqualConstraint;

@ResetPasswordEqualConstraint(message = "Reset - Password are not equal")
public class PasswordDto {

    @NotNull(message = "Password rest request tampered - id")
	private Long id;
	
    @NotNull(message = "Password rest request tampered - token")
    @NotEmpty(message = "Password rest request tampered - token")
	private String token;

    @NotNull(message = "Please provide new password - Not null")
    @NotEmpty(message = "Please provide new password - Not empty")
	private String newPassword;
	
    @NotNull(message = "Please provide confirm new password - Not null")
	private String confirmNewPassword;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}

	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}

}
