package com.omrtb.restjpa.error;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// error handle for @Valid
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", status.value());

		// Get all errors
		List<FieldError> feLst = ex.getBindingResult().getFieldErrors();
		List<ObjectError> obLst = null;
		List<String> errors = null;
		if (feLst == null || feLst.isEmpty()) {
			obLst = ex.getBindingResult().getGlobalErrors();
			errors = obLst.stream().map(x -> x.getDefaultMessage()).collect(Collectors.toList());
		} else {
			errors = feLst.stream().map(x -> x.getDefaultMessage()).collect(Collectors.toList());
		}

		body.put("errors", errors);

		return new ResponseEntity<>(body, headers, status);

	}

	/*
	 * @ExceptionHandler(ConstraintViolationException.class) public void
	 * constraintViolationException(HttpServletResponse response) throws IOException
	 * { response.sendError(HttpStatus.BAD_REQUEST.value()); }
	 */

	@ExceptionHandler({ ConstraintViolationException.class })
	public ResponseEntity<Object> handleConstraintViolation(Exception ex, WebRequest request) {
		Throwable cause = ((ConstraintViolationException) ex).getCause();
		if (cause instanceof ConstraintViolationException) {

			ConstraintViolationException consEx = (ConstraintViolationException) cause;
			final List<String> errors = new ArrayList<String>();
			for (final ConstraintViolation<?> violation : consEx.getConstraintViolations()) {
				errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
			}

			// final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
			// consEx.getLocalizedMessage(), errors);
			return new ResponseEntity<Object>(cause.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return null;
	}

	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

		Throwable thr = ex.getCause();
		final List<String> errors = new ArrayList<String>();
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", new Date());
		body.put("status", status.value());
		if(thr instanceof InvalidFormatException) {
			errors.add(((InvalidFormatException) thr).getPathReference() + ": " + thr.getLocalizedMessage());
		}
		body.put("errors", errors);

		return new ResponseEntity<>(body, headers, status);
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseBody
	public ViolationResponse handleConflictException(DataIntegrityViolationException ex) throws Exception {
		return new ViolationResponse(ex.getRootCause().getLocalizedMessage());
	}
}
