package com.omrtb.restjpa.validations;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MyDateValidator implements ConstraintValidator<ValidateDate, Date> {
   public void initialize(ValidateDate constraint) {
   }

   public boolean isValid(Date value, ConstraintValidatorContext context) {
      return value!=null && !value.after(new Date());
   }
}