package com.omrtb.restjpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class BooleanToIntegerConverter implements AttributeConverter<Boolean, Integer> {

	@Override
	public Integer convertToDatabaseColumn(Boolean value) {
		if (value == null)
			return null;
		else
			return value ? 1 : 0;
	}

	@Override
	public Boolean convertToEntityAttribute(Integer value) {
		if (value == null)
			return null;
		else if (value.equals(1))
			return true;
		else if (value.equals(0))
			return false;
		else
			throw new IllegalStateException("Invalid boolean character: " + value);
	}
}