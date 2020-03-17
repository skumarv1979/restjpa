package com.omrtb.restjpa.request.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.omrtb.restjpa.validations.Email;

public class ContactUs {

    @NotNull(message = "Please provide First Name - cannot be null")
    @NotEmpty(message = "Please provide First Name - cannot be blank")
	private String firstName;
	
	private String lastName;
	
    @NotNull(message = "Please provide Sender email - cannot be null")
    @NotEmpty(message = "Please provide Sender email - cannot be blank")
    @Email(message = "Invalid sender email")
	private String senderEmail;
	
    @NotNull(message = "Please provide subject - cannot be null")
    @NotEmpty(message = "Please provide subject - cannot be blank")
	private String subject;
	
    @NotNull(message = "Please provide Receiver email - cannot be null")
    @NotEmpty(message = "Please provide Receiver email - cannot be blank")
    @Email(message = "Invalid Receiver email")
	private String receiverEmail;
	
    @NotNull(message = "Please provide message - cannot be null")
    @NotEmpty(message = "Please provide message - cannot be blank")
	private String message;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getReceiverEmail() {
		return receiverEmail;
	}

	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
