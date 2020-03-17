package com.omrtb.restjpa.entity.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class MyActivityTypeSerializer extends StdSerializer<ActivityType> {

	protected MyActivityTypeSerializer() {
		super(MyActivityTypeSerializer.class, false);
	}
	protected MyActivityTypeSerializer(Class<ActivityType> t) {
		super(t);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void serialize(ActivityType activityType, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeStartObject();
		generator.writeFieldName("name");
		generator.writeString(activityType.toString());
		generator.writeEndObject();
	}

}
