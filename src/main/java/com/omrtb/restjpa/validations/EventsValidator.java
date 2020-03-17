package com.omrtb.restjpa.validations;


import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Strings;
import com.omrtb.restjpa.entity.model.Events;
import com.omrtb.restjpa.entity.model.User;

@Component("beforeCreateEventsValidator")
public class EventsValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Events.class.equals(clazz) || User.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		if(obj instanceof Events) {
		Events events = (Events) obj;
		Date now = new Date();
        if (Strings.isNullOrEmpty(events.getName())) {
            errors.rejectValue("name", "events.empty.name");
        }
        else if (events.getStartDate()==null) {
            errors.rejectValue("startDate", "events.empty.startdate");
        }
        else if (events.getEndDate()==null) {
            errors.rejectValue("startDate", "events.empty.enddate");
        }
        else if(events.getStartDate().before(now)) {
            errors.rejectValue("startDate", "events.invalid.paststartdate");
        }
        else if (events.getStartDate().after(events.getEndDate())) {
            errors.rejectValue("startDate", "events.invalid.startenddate");
        }
		}
		else if(obj instanceof User) {
			//User user = (User) obj;
		}

	}

}