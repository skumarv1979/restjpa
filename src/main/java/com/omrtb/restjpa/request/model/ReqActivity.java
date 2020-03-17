package com.omrtb.restjpa.request.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.omrtb.restjpa.validations.ActivityTypeValidation;

public class ReqActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private String sourceData;
	
	@JsonIgnore
    private long activityId;
    
	@NotNull(message = "Please provide Date")
    private Date startDate;
    
    private Float distance;
    
    private Integer movingTime;

    private Integer elapsedTime;
    
    private String workoutName;
    
    private String workoutType;

    @ActivityTypeValidation(message = "Activity type can be Run, Ride, Swim")
    @NotNull(message = "Activity Type cannot be null")
    private String activityType;
    
	public String getSourceData() {
		return sourceData;
	}

	public void setSourceData(String sourceData) {
		this.sourceData = sourceData;
	}

	public long getActivityId() {
		return activityId;
	}

	public void setActivityId(long activityId) {
		this.activityId = activityId;
	}

	public Date getStartDate() {
		return startDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
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

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

    
}
