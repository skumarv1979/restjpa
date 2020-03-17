package com.omrtb.restjpa.request.model;

import java.io.Serializable;
import java.util.Date;

import com.omrtb.restjpa.entity.model.Events;

public class ResponseEvent implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

    private String name;
    
    private Date startDate;

    private Date endDate;
    
    private String status;

    private boolean isRegistered;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}
    
	public void copyEventEntityToRepsonse(Events event, boolean isRegistered) {
		this.setId(event.getId());
		this.setName(event.getName());
		this.setStartDate(event.getStartDate());
		this.setEndDate(event.getEndDate());
		this.setStatus(event.getStatus());
		this.setRegistered(isRegistered);
	}
}
