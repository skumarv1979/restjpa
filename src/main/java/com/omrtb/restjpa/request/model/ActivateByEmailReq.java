package com.omrtb.restjpa.request.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class ActivateByEmailReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "provide Email to activate")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ActivateByEmailReq [email=" + email + "]";
	}
	
	
}
