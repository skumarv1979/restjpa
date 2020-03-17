package com.omrtb.restjpa.entity.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
@JsonSerialize(using = MyActivityTypeSerializer.class)
public enum ActivityType {

	ALPINE_SKIING(1, "Alpine skiing"),
	BACKCOUNTRY_SKIING(2, "Backcountry skiing (off-piste)"),
	CANOEING(3, "Canoeing"),
	CROSSFIT(4, "Crossfit"),
	EBIKE_RIDE(5, "E-Bike Ride"),
	ELLIPTICAL_TRAINER(6, "Elliptical Trainer"),
	HIKE(7, "Hike"),
	ICE_SKATING(8, "Ice skating"),
	INLINE_SKATING(9, "Inline skating"),
	KAYAKING(10, "Kayaking"),
	KITE_SURFING(11, "Kite surfing"),
	NORDIC_SKIING(12, "Nordic skiing"),
	RIDE(13, "Ride"),
	ROCK_CLIMBING(14, "Rock Climbing"),
	ROLLER_SKIING(15, "Roller skiing"),
	ROWING(16, "Rowing"),
	RUN(17, "Run"),
	SNOWBOARDING(18, "Snowboarding"),
	SNOWSHOEING(19, "Snowshoeing"),
	STAIR_STEPPER(20, "Stair Stepper"),
	STANDUP_PADDLING(21, "Stand-Up Paddling"),
	SURFING(22, "Surfing"),
	SWIM(23, "Swim"),
	VIRTUAL_RIDE(24, "Virtual Ride"),
	WALK(25, "Walk"),
	WEIGHT_TRAINING(26, "Weight Training"),
	WINDSURFING(27, "Windsurfing"),
	WORKOUT(28, "Workout"),
	YOGA(29, "Yoga"),
	REST(30, "Rest Day");
	
	private int value;
	private String desc;
	private ActivityType(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	public int getValue() {
		return this.value;
	}
	public String getDesc() {
		return this.desc;
	}
    public static ActivityType get(String type) {
        return lookup.get(type);
    }
    public static ActivityType getByKey(Integer type) {
        return lookupByKey.get(type);
    }
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return this.getDesc();
    }
	private static final Map<String, ActivityType> lookup = new HashMap<String, ActivityType>();
	private static final Map<Integer, ActivityType> lookupByKey = new HashMap<Integer, ActivityType>();
    static {
        for (ActivityType d : ActivityType.values()) {
            lookup.put(d.getDesc(), d);
            lookupByKey.put(d.getValue(), d);
        }
    }
}
