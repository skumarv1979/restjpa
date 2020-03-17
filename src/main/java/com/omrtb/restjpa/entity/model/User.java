package com.omrtb.restjpa.entity.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.omrtb.restjpa.converter.BooleanToIntegerConverter;
import com.omrtb.restjpa.converter.UserStatusToStringConverter;
import com.omrtb.restjpa.validations.Email;
import com.omrtb.restjpa.validations.PasswordsEqualConstraint;
import com.omrtb.restjpa.validations.ValidateDate;

import lombok.Data;

@Entity

@Data
@Access(value=AccessType.FIELD)
@Table(
	    name="USER", 
	    uniqueConstraints=
	        @UniqueConstraint(columnNames={"email"})
	    )
@PasswordsEqualConstraint(message = "Password are not equal")
public class User implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "VARCHAR(20)")
    //@Pattern(regexp = "[A-Za-z0-9_]{5,20}", flags = Flag.UNICODE_CASE, message = "User Id should be minumum of 5 charactes and maximum od 20 characters, can contain alphanumeric chars and underscore")
    private String userId;

    @Column(name = "name", columnDefinition = "VARCHAR(200)")
    @NotEmpty(message = "Please provide a name")
    private String name;

    @Column(name = "address", columnDefinition = "VARCHAR(600)")
    private String address;

    @Column(name = "pincode", columnDefinition = "INT(6)")
    private BigDecimal pincode;

    @Column(name = "mobile", columnDefinition = "CHAR(20)")
    private String mobile;

    @Column(name = "email", columnDefinition = "VARCHAR(150)")
    @NotEmpty(message = "Please provide email")
    @Email(message = "Invalid email")
    private String email;

    @Column(name = "bloodgroup", columnDefinition = "VARCHAR(6)")
    private String bloodgroup;

	@ValidateDate(message = "Date of Birth cannot be future date")
    private Date dob;

    @Column(name = "gender", columnDefinition = "VARCHAR(10)")
    private String gender;

    @Column(name = "tshirt", columnDefinition = "CHAR(4)")
    private String tshirt;

    @Column(name = "venue", columnDefinition = "VARCHAR(100)")
    private String venue;

    @Column(name = "cycling", columnDefinition = "INT(1)")
    @Convert(converter=BooleanToIntegerConverter.class)
    private Boolean cycling;

    @Column(name = "status", columnDefinition = "INT(1)")
    @Convert(converter=UserStatusToStringConverter.class)
    @JsonIgnore
    private UserStatus status;

    @JsonIgnore
    private Long managedBy;

    @Column(name = "password", columnDefinition = "VARCHAR(100)")
    @NotEmpty(message = "Please provide password")
    private String password;

    @Transient
    private String confirmPassword;

    @OneToOne(fetch = FetchType.EAGER, mappedBy="user")
	@Cascade(value = org.hibernate.annotations.CascadeType.ALL)
	@JsonIgnoreProperties("user")
    //@JsonIgnore
    private StravaUser stravaUser;
    
    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name="USER_EVENTS_REGISTRATION", joinColumns={@JoinColumn(referencedColumnName="ID")}
    , inverseJoinColumns={@JoinColumn(referencedColumnName="ID")})
	@JsonIgnoreProperties("users")
    //@JsonIgnore
    private Set<Events> events;

    @OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="USER_ID")
	//@JsonIgnoreProperties("user")
    @JsonIgnore
    @SortNatural
    private SortedSet<Activity> activities = new TreeSet<Activity>();
    
    @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable( 
        name = "USERS_ROLES", 
        joinColumns = @JoinColumn(
          name = "user_id", referencedColumnName = "id"), 
        inverseJoinColumns = @JoinColumn(
          name = "role_id", referencedColumnName = "id")) 
    @JsonIgnore
    private Set<Role> roles;

    @OneToOne(fetch = FetchType.EAGER, mappedBy="user")
	@Cascade(value = org.hibernate.annotations.CascadeType.REMOVE)
	@JsonIgnore
    private PasswordResetToken passwordResetToken;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
    
    private String approvedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	@JsonIgnore
	public String getConfirmPassword() {
		return confirmPassword;
	}

	@JsonProperty
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public StravaUser getStravaUser() {
		return stravaUser;
	}

	public void setStravaUser(StravaUser stravaUser) {
		this.stravaUser = stravaUser;
	}

	public SortedSet<Activity> getActivities() {
		return activities;
	}

	public void setActivities(SortedSet<Activity> activities) {
		this.activities = (SortedSet<Activity>) activities;
	}

	public Long getManagedBy() {
		return managedBy;
	}

	public void setManagedBy(Long managedBy) {
		this.managedBy = managedBy;
	}

	public Set<Events> getEvents() {
		return events;
	}

	public void setEvents(Set<Events> events) {
		this.events = events;
	}

	
	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public PasswordResetToken getPasswordResetToken() {
		return passwordResetToken;
	}

    public void setPasswordResetToken(PasswordResetToken passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return email.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null && obj instanceof User) {
			User u = (User)obj;
			return this.getEmail().equals(u.getEmail());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", address=" + address + ", pincode=" + pincode + ", mobile="
				+ mobile + ", email=" + email + ", bloodgroup=" + bloodgroup + ", dob=" + dob + ", gender=" + gender
				+ ", tshirt=" + tshirt + ", venue=" + venue + ", cycling=" + cycling + ", status=" + status 
				+ ", stravaUser=" + stravaUser + ", managedBy=" + managedBy + ", password=" + password
				+ ", confirmPassword=" + confirmPassword + ", events=" + events + ", activities=" + activities
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}