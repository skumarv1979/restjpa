package com.omrtb.restjpa.request.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.omrtb.restjpa.entity.model.User;
import com.omrtb.restjpa.validations.Email;
import com.omrtb.restjpa.validations.ValidateDate;

public class UpdateUser implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//@NotEmpty(message = "Please provide a userId")
    //@Pattern(regexp = "[A-Za-z0-9_]{5,20}", flags = Flag.UNICODE_CASE, message = "User Id should be minumum of 5 charactes and maximum od 20 characters, can contain alphanumeric chars and underscore")
    private String userId;

    @NotEmpty(message = "Please provide a name")
    private String name;

    private String address;

    private BigDecimal pincode;

    private String mobile;

    @NotEmpty(message = "Please provide email")
    @Email(message = "Invalid email")
    private String email;

    private String bloodgroup;

	@ValidateDate(message = "Date of Birth cannot be future date")
    private Date dob;

    private String gender;

    private String tshirt;

    private String venue;

    private Boolean cycling;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getPincode() {
		return pincode;
	}

	public void setPincode(BigDecimal pincode) {
		this.pincode = pincode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBloodgroup() {
		return bloodgroup;
	}

	public void setBloodgroup(String bloodgroup) {
		this.bloodgroup = bloodgroup;
	}

	public Date getDob() {
		return dob;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTshirt() {
		return tshirt;
	}

	public void setTshirt(String tshirt) {
		this.tshirt = tshirt;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public Boolean getCycling() {
		return cycling;
	}

	public void setCycling(Boolean cycling) {
		this.cycling = cycling;
	}

	public void copyToPersist(User user) {
		user.setName(this.getName());
		user.setAddress(this.getAddress());
		user.setPincode(this.getPincode());
		user.setMobile(this.getMobile());
		user.setEmail(this.getEmail());;
		user.setBloodgroup(this.getBloodgroup());;
		user.setDob(this.getDob());
		user.setGender(this.getGender());
		user.setTshirt(this.getTshirt());
		user.setVenue(this.getVenue());
		user.setCycling(this.getCycling());
	}
}
