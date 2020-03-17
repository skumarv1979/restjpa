package com.omrtb.restjpa.entity.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = MyUserStatusSerializer.class)
public enum UserStatus {
	NEW(1,"New"),
	ACTIVE(2, "Active"),
	INACTIVE(3, "Inactive");
	private int value;
	private String desc;
	private UserStatus(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	public int getValue() {
		return this.value;
	}
	public String getDesc() {
		return this.desc;
	}
    public static UserStatus get(String type) {
        return lookup.get(type);
    }
    public static UserStatus getByKey(Integer type) {
        return lookupByKey.get(type);
    }
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return this.getDesc();
    }
	private static final Map<String, UserStatus> lookup = new HashMap<String, UserStatus>();
	private static final Map<Integer, UserStatus> lookupByKey = new HashMap<Integer, UserStatus>();
    static {
        for (UserStatus d : UserStatus.values()) {
            lookup.put(d.getDesc(), d);
            lookupByKey.put(d.getValue(), d);
        }
    }

}
