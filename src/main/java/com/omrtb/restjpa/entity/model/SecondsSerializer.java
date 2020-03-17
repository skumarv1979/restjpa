package com.omrtb.restjpa.entity.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class SecondsSerializer extends StdSerializer<UserStatus> {

	protected SecondsSerializer() {
		super(SecondsSerializer.class, false);
	}
	protected SecondsSerializer(Class<UserStatus> t) {
		super(t);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void serialize(UserStatus userStatus, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeStartObject();
		generator.writeFieldName("name");
		generator.writeString(userStatus.toString());
		generator.writeEndObject();
	}

}
