package com.omrtb.restjpa.request.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ReturnResult implements Serializable {

	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 1L;
	
	private String result;

	public ReturnResult() {
		// TODO Auto-generated constructor stub
	}
	
	public ReturnResult(String result) {
		this.result = result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
