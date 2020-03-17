package com.omrtb.restjpa.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omrtb.restjpa.entity.model.User;
import com.omrtb.restjpa.repository.UserRepository;
import com.omrtb.restjpa.request.model.ReturnResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/forgot")
@Slf4j
@RequiredArgsConstructor
public class ForgotUserController {

	@Autowired
	private UserRepository userRepository;

	@Value("#{'${spring.mail.username}'}")
	private String senderEmail;
	
	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/emailorphone/{email:.+}")
	public ResponseEntity<ReturnResult> forgotUser(@PathVariable String email) {
		List<User> users = userRepository.findByEmail(email);
		List<String> userIds = new ArrayList<String>();
		List<String> userNames = new ArrayList<String>();
		String message = null;
		if(users == null || users.isEmpty()) {
			users = userRepository.findByMobile(email);
		}
		if (users != null && !users.isEmpty()) {
			for (User user : users) {
				userIds.add(user.getUserId());
				userNames.add(user.getName());
			}
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(email);
			msg.setSubject("OMRTB - Forgot User");
			msg.setText("Hello "+String.join(",", userNames)+"\n The follwoing is your user id " + String.join(",", userIds));
			javaMailSender.send(msg);
			message = "Mail has been sent, please refer you inbox";
		}
		else {
			message = "Couldn't find the user Id";
		}

		return ResponseEntity.ok(new ReturnResult(message));
	}

}
