package com.omrtb.restjpa.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omrtb.restjpa.entity.model.Events;
import com.omrtb.restjpa.entity.model.Role;
import com.omrtb.restjpa.entity.model.User;
import com.omrtb.restjpa.entity.model.UserStatus;
import com.omrtb.restjpa.repository.EventsRepository;
import com.omrtb.restjpa.repository.RoleRepository;
import com.omrtb.restjpa.repository.UserRepository;
import com.omrtb.restjpa.request.model.ActivateByEmailReq;
import com.omrtb.restjpa.request.model.ReturnResult;
import com.omrtb.restjpa.utils.RoleSingleton;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/authops/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {

	@Value("#{'${spring.mail.properties.mail.admins.additional.ccemail}'.split(',')}")
	private List<String> adminsEmail;

	@Value("#{'${spring.mail.properties.mail.admin.email}'}")
	private String fromEmail;
	
	@Value("#{'${web.client.login.page}'}")
	private String loginUrl;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private EventsRepository eventsRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	private static Logger LOGGER = LogManager.getLogger(AdminController.class);
	@GetMapping("/getallusers")
	public ResponseEntity<Iterable<User>> findAll() {
		return ResponseEntity.ok(userRepository.findAll());
	}

	@PostMapping(value="/activate")
	public ResponseEntity<ReturnResult> activate(HttpServletRequest request, @AuthenticationPrincipal PdfUserDetails pdfUser, @Valid @RequestBody ActivateByEmailReq reqObj) {
		LOGGER.error("What is the input :: "+reqObj);
		List<User> userList = userRepository.findByEmail(reqObj.getEmail());
		if(userList==null || userList.isEmpty()) {
			return ResponseEntity.badRequest().body(new ReturnResult("No user found to activate!!"));
		}
		else if(userList.size()>1) {
			return ResponseEntity.badRequest().body(new ReturnResult("Too many users found to activate!!"));
		}
		else if(!pdfUser.getUser().getEmail().equals(reqObj.getEmail())) {
			RoleSingleton roleSingleton = RoleSingleton.getInstance(roleRepository);
			Role role = roleSingleton.getRole("USER");
			User user = userList.get(0);
			Set<Role> roles = user.getRoles();
			if(roles==null) {
				roles = new HashSet<Role>();
				user.setRoles(roles);
			}
			Set<User> users = role.getUsers();
			if(users==null) {
				users = new HashSet<User>();
				role.setUsers(users);
			}
			users.add(user);
			roles.add(role);
			user.setStatus(UserStatus.ACTIVE);
			User usr = userRepository.save(user);
			if(usr !=null) {
				javaMailSender.send(constructActivationEmail(request.getLocale(), usr));
				return ResponseEntity.ok().body(new ReturnResult("Activated"));
			}
			return ResponseEntity.badRequest().body(new ReturnResult("Unable to activate"));
		}
		else {
			return ResponseEntity.badRequest().body(new ReturnResult("Unable to activate - no self approvals"));
		}
	}
	//@PostMapping("/activatebyid/{id}")
	public ResponseEntity<ReturnResult> activate(HttpServletRequest request, @AuthenticationPrincipal PdfUserDetails pdfUser, @PathVariable Long id) {
		Optional<User> userList = userRepository.findById(id);
		if(userList.isPresent()) {
			RoleSingleton roleSingleton = RoleSingleton.getInstance(roleRepository);
			Role role = roleSingleton.getRole("USER");
			User user = userList.get();
			Set<Role> roles = user.getRoles();
			if(roles!=null) {
				roles = new HashSet<Role>();
				user.setRoles(roles);
			}
			Set<User> users = role.getUsers();
			if(users!=null) {
				users = new HashSet<User>();
				role.setUsers(users);
			}
			users.add(user);
			roles.add(role);
			user.setStatus(UserStatus.ACTIVE);
			User usr = userRepository.save(user);
			if(usr !=null) {
				javaMailSender.send(constructActivationEmail(request.getLocale(), usr));
				return ResponseEntity.ok().body(new ReturnResult("Activated"));
			}
			return ResponseEntity.badRequest().body(new ReturnResult("Unable to activate"));
		}
		else {
			return ResponseEntity.badRequest().body(new ReturnResult("No users found - Unable to activate"));
		}
	}
	private SimpleMailMessage constructActivationEmail(Locale locale, User user) {
		String message = "Hi "+user.getName()+",\r\n\r\nYour account has been activated.\r\n\r\nLogin to pur app and update your activities.\r\n\r\n"+loginUrl+"\r\n\r\n";
		return constructActEmail("OMRTB - Account Activated", message + "\r\n\r\nCheers\r\nOMRTB Team", user);
	}

	private SimpleMailMessage constructActEmail(String subject, String body, User user) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject(subject);
		email.setText(body);
		email.setTo(user.getEmail());
		String[] adminsEmailAr = new String[adminsEmail.size()];
		email.setCc(adminsEmail.toArray(adminsEmailAr));
		LOGGER.info(">>>>>>>>>>>>>"+fromEmail+"<<<<<<<<<<<");
		email.setFrom(fromEmail);
		return email;
	}

	@GetMapping("/getallnewusers")
	public ResponseEntity<Iterable<User>> findAllNewUsers() {
		return ResponseEntity.ok(userRepository.findAllNewUsers());
	}
	
	@PostMapping("/create/event")
	public ResponseEntity<Events> create(@AuthenticationPrincipal PdfUserDetails pdfUser, @Valid @RequestBody Events events) {
		events.setStatus("Open");
		events.setCreatedBy(pdfUser.getUser().getId());
		Events event = eventsRepository.save(events);
		return ResponseEntity.ok(event);
	}

}
