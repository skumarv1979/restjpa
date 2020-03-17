package com.omrtb.restjpa.entity.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.omrtb.restjpa.converter.ActivityTypeToStringConverter;
import com.omrtb.restjpa.converter.SecondsToHoursMinutesSecondsConverter;

import lombok.Data;

@Entity

@Data
@Access(value=AccessType.FIELD)
public class Activity implements Serializable, Comparable<Activity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
	@JsonIgnore
	private String sourceData;
	
	@JsonIgnore
    private Long activityId;
    
    private ZonedDateTime startDate;
    
    @NotEmpty(message = "Please provide Date")
    private LocalDateTime startDateLocal;
    
    private String timezone;
    
    private String type;
    
    //@Column(name = "moving_time", columnDefinition = "INT(8)")
    //@Convert(converter = SecondsToHoursMinutesSecondsConverter.class)
    private Integer movingTime;
    
    //@Column(name = "elapsed_time", columnDefinition = "INT(8)")
    //@Convert(converter = SecondsToHoursMinutesSecondsConverter.class)
    private Integer elapsedTime;
    
    private Float distance;
    
    private Float totalElevationGain;
    
    private Boolean commute;
    
    @JsonIgnore
    private Boolean manual;
    
    private Float calories;
    
    @Convert(converter=ActivityTypeToStringConverter.class)
    private ActivityType activityType;
    
    private String workoutName;
    
    private String workoutType;
    @Override
    public int hashCode() {
    	// TODO Auto-generated method stub
    	return (sourceData+activityId).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
    	// TODO Auto-generated method stub
    	if(obj !=null && obj instanceof Activity) {
    		Activity acti = (Activity)obj;
    		if(SourceActivity.STRAVA.getDesc().equals(this.sourceData)) {
    			return this.sourceData.equals(acti.getSourceData()) && this.activityId.equals(acti.getActivityId());
    		}
    		else {
    			return this.sourceData.equals(acti.getSourceData()) && this.startDate.equals(acti.getStartDate());
    		}
    	}
    	return false;
    }
	
    @ManyToOne(fetch = FetchType.EAGER)
	//@JsonIgnoreProperties("activities")
    @JsonIgnore
    private User user;
    
    @ManyToMany(mappedBy="activities")
	//@JsonIgnoreProperties("activities")
    @JsonIgnore
    private Set<Events> events;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = sourceData;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getMovingTime() {
		return movingTime;
	}

	public void setMovingTime(Integer movingTime) {
		this.movingTime = movingTime;
	}

	public Integer getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(Integer elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getStartDateLocal() {
		return startDateLocal;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	public void setStartDateLocal(LocalDateTime startDateLocal) {
		this.startDateLocal = startDateLocal;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	public Float getTotalElevationGain() {
		return totalElevationGain;
	}

	public void setTotalElevationGain(Float totalElevationGain) {
		this.totalElevationGain = totalElevationGain;
	}

	public Boolean getCommute() {
		return commute;
	}

	public void setCommute(Boolean commute) {
		this.commute = commute;
	}

	public Boolean getManual() {
		return manual;
	}

	public void setManual(Boolean manual) {
		this.manual = manual;
	}

	public Float getCalories() {
		return calories;
	}

	public void setCalories(Float calories) {
		this.calories = calories;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getWorkoutName() {
		return workoutName;
	}

	public void setWorkoutName(String workoutName) {
		this.workoutName = workoutName;
	}

	public String getWorkoutType() {
		return workoutType;
	}

	public void setWorkoutType(String workoutType) {
		this.workoutType = workoutType;
	}

	public Set<Events> getEvents() {
		return events;
	}

	public void setEvents(Set<Events> events) {
		this.events = events;
	}

	@Override
	public int compareTo(Activity o) {
		return o.getStartDate().compareTo(this.getStartDate());
	}
    
}
