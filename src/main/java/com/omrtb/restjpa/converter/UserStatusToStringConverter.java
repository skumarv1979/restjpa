package com.omrtb.restjpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.omrtb.restjpa.entity.model.UserStatus;

@Converter
public class UserStatusToStringConverter implements AttributeConverter<UserStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(UserStatus value) {
		if (value == null)
			return null;
		else
			return value.getValue();
	}

	@Override
	public UserStatus convertToEntityAttribute(Integer value) {
		if (value == null)
			return null;
		else //if(ActivityType.get(value)!=null)
			return UserStatus.getByKey(value);
		//else
		//	throw new IllegalStateException("Invalid boolean character: " + value);
	}
}