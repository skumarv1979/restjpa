package com.omrtb.restjpa.entity.model;

import java.util.HashMap;
import java.util.Map;

public enum SourceActivity {
	OMRTB(0, "OMRTB"),
	STRAVA(1, "STRAVA");
	private int value;
	private String desc;
	SourceActivity(int i, String desc) {
		this.value = i;
		this.desc = desc;
	}
	public int getValue() {
		return this.value;
	}
	public String getDesc() {
		return this.desc;
	}
    public static SourceActivity get(String type) {
        return lookup.get(type);
    }
	private static final Map<String, SourceActivity> lookup = new HashMap<String, SourceActivity>();
    static {
        for (SourceActivity d : SourceActivity.values()) {
            lookup.put(d.getDesc(), d);
        }
    }
}
