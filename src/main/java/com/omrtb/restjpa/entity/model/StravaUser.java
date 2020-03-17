package com.omrtb.restjpa.entity.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Entity

@Data
@Access(value=AccessType.FIELD)
public class StravaUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(generator = "gen")
	@GenericGenerator(name = "gen", strategy = "foreign", parameters = { @Parameter(name = "property", value = "user") })
	@JsonBackReference
	private Long id;
	private Integer atheleteId;
	@JsonIgnore
	private String code;
	@JsonIgnore
	private String state;
	@JsonIgnore
	private String scope;
	@JsonIgnore
	private String error;
	@JsonIgnore
	private String refreshToken;
	@JsonIgnore
	private String accessToken;
	@JsonIgnore
	private long expiresIn;
	@JsonIgnore
	private Date expiresAt;
	private String profile;
	private String profileMedium;
    @Transient
	private String authorizationStatus;
    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
	

	@OneToOne(fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	//@JsonIgnoreProperties("stravaUser")
	@JsonIgnore
	private User user;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAtheleteId() {
		return atheleteId;
	}
	public void setAtheleteId(Integer atheleteId) {
		this.atheleteId = atheleteId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
	public Date getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getProfileMedium() {
		return profileMedium;
	}
	public void setProfileMedium(String profileMedium) {
		this.profileMedium = profileMedium;
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
	@JsonProperty
	public String getAuthorizationStatus() {
		return authorizationStatus;
	}
	@JsonIgnore
	public void setAuthorizationStatus(String authorizationStatus) {
		this.authorizationStatus = authorizationStatus;
	}
	public void copy(StravaUser stravaUser) {
		this.setAtheleteId(stravaUser.getAtheleteId());
		this.setCode(stravaUser.getCode());
		this.setState(stravaUser.getState());
		this.setScope(stravaUser.getScope());
		this.setError(stravaUser.getError());
		this.setAccessToken(stravaUser.getAccessToken());
		this.setRefreshToken(stravaUser.getRefreshToken());
		this.setExpiresIn(stravaUser.getExpiresIn());
		this.setExpiresAt(stravaUser.getExpiresAt());
		this.setProfile(stravaUser.getProfile());
		this.setProfileMedium(stravaUser.getProfileMedium());
	}
	@Override
	public String toString() {
		return "StravaUser [id=" + id + ", atheleteId=" + atheleteId + ", code=" + code + ", state=" + state
				+ ", scope=" + scope + ", error=" + error + ", refreshToken=" + refreshToken + ", accessToken="
				+ accessToken + ", expiresIn=" + expiresIn + ", expiresAt=" + expiresAt + ", profile=" + profile
				+ ", profileMedium=" + profileMedium + ", authorizationStatus=" + authorizationStatus + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + ", user=" + user + "]";
	}
	
}
