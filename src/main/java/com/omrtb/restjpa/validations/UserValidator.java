package com.omrtb.restjpa.validations;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.google.common.base.Strings;
import com.omrtb.restjpa.entity.model.StravaUser;
import com.omrtb.restjpa.entity.model.User;

@Component("beforeCreateUserValidator")
public class UserValidator implements Validator {
	 
    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz) || StravaUser.class.equals(clazz);
    }
 
    @Override
    public void validate(Object obj, Errors errors) {
    	if(obj instanceof User) {
	    	User user = (User) obj;
	    
	        if (checkInputString(user.getEmail())) {
	            errors.rejectValue("email", "email.empty");
	        }
	        else if(!isValid(user.getEmail())) {
	        	errors.rejectValue("email", "email.invalid");
	        }
	        else if(Strings.isNullOrEmpty(user.getPassword())) {
	        	errors.rejectValue("password", "password.empty");
	        }
	        else if(Strings.isNullOrEmpty(user.getConfirmPassword())) {
	        	errors.rejectValue("confirmPassword", "confirmpassword.empty");
	        }
	        else if(!user.getPassword().equals(user.getConfirmPassword())) {
	        	errors.rejectValue("password", "confirmpassword.mismatch");
	        }
    	}
    	/*else if(obj instanceof StravaUser) {
    		
    	}*/
    }
 
    private boolean checkInputString(String input) {
        return (input == null || input.trim().length() == 0);
    }
    private boolean isValid(String email) 
    { 
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                            "[a-zA-Z0-9_+&*-]+)*@" + 
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                            "A-Z]{2,7}$"; 
                              
        Pattern pat = Pattern.compile(emailRegex); 
        if (email == null) 
            return false; 
        return pat.matcher(email).matches(); 
    } 
}