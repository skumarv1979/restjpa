package com.omrtb.restjpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.omrtb.restjpa.entity.model.ActivityType;

@Converter
public class ActivityTypeToStringConverter implements AttributeConverter<ActivityType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(ActivityType value) {
		if (value == null)
			return null;
		else
			return value.getValue();
	}

	@Override
	public ActivityType convertToEntityAttribute(Integer value) {
		if (value == null)
			return null;
		else //if(ActivityType.get(value)!=null)
			return ActivityType.getByKey(value);
		//else
		//	throw new IllegalStateException("Invalid boolean character: " + value);
	}
}