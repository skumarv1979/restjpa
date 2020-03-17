package com.omrtb.restjpa.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.omrtb.restjpa.RestjpaApplication;
import com.omrtb.restjpa.entity.model.PasswordResetToken;
import com.omrtb.restjpa.entity.model.Role;
import com.omrtb.restjpa.entity.model.StravaUser;
import com.omrtb.restjpa.entity.model.User;
import com.omrtb.restjpa.entity.model.UserStatus;
import com.omrtb.restjpa.repository.PasswordResetTokenRepository;
import com.omrtb.restjpa.repository.RoleRepository;
import com.omrtb.restjpa.repository.UserRepository;
import com.omrtb.restjpa.request.model.ChangePasswordResponse;
import com.omrtb.restjpa.request.model.ContactUs;
import com.omrtb.restjpa.request.model.ForgotPasswordEmail;
import com.omrtb.restjpa.request.model.PasswordDto;
import com.omrtb.restjpa.request.model.ReturnResult;
import com.omrtb.restjpa.utils.RoleSingleton;
import com.omrtb.restjpa.utils.StravaUtils;
import com.opencsv.CSVReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/unauthops")
@Slf4j
@RequiredArgsConstructor
public class UnAuthenticatedOpsController {

	@Value("#{'${spring.mail.properties.mail.admins.additional.ccemail}'.split(',')}")
	private List<String> adminsEmail;

	@Value("#{'${spring.mail.properties.mail.admin.email}'}")
	private String fromEmail;
	
	@Autowired
	private PasswordEncoder  encoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Value("#{'${spring.mail.username}'}")
	private String senderEmail;
	
	@Value("#{'${forgot.password.domain}'}")
	private String domainName;
	
	private static Logger LOGGER = LogManager.getLogger(UnAuthenticatedOpsController.class);
	@PostMapping("/register")
	public ResponseEntity<User> create(HttpServletRequest request, @Valid @RequestBody User user) {
		String encryptedPwd = encoder.encode(user.getPassword());
		user.setPassword(encryptedPwd);
		user.setConfirmPassword(null);
		user.setStatus(UserStatus.NEW);
		StravaUser su = user.getStravaUser();
		if(su!=null) {
			su.setUser(user);
		}
		User usr = userRepository.save(user);
		javaMailSender.send(constructRegistrationEmail(request.getLocale(), usr));
		return ResponseEntity.ok(usr);
	}

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
			String[] adminsEmailAr = new String[adminsEmail.size()];
			msg.setCc(adminsEmail.toArray(adminsEmailAr));
			LOGGER.info(">>>>>>>>>>>>>"+fromEmail+"<<<<<<<<<<<");
			msg.setFrom(fromEmail);
			javaMailSender.send(msg);
			message = "Mail has been sent, please refer you inbox";
		}
		else {
			message = "Couldn't find the user Id";
		}

		return ResponseEntity.ok(new ReturnResult(message));
	}
	@GetMapping("/useridexists/{userid:.+}")
	public ResponseEntity<ReturnResult> userIdInUse(@PathVariable String userid) {
        Optional<User> optionalUser = userRepository.findByUserId(userid);
        return ResponseEntity.ok(new ReturnResult(Boolean.toString(optionalUser.isPresent())));
	}
	@Autowired
	private StravaUtils stravaUtils;
	@GetMapping("/pullstrava")
	public ResponseEntity<ReturnResult> pullstrava() {
		stravaUtils.updateStravaActivity();
        return ResponseEntity.ok(new ReturnResult("Refer Console"));
	}
	
	@PostMapping("/resetpassword")
	public ResponseEntity<ReturnResult> resetPassword(HttpServletRequest request, @Valid @RequestBody ForgotPasswordEmail forgotPwdEmail) {
		Optional<User> optUser = userRepository.findUniqueUserByEmail(forgotPwdEmail.getEmail());
		String message = null;
		if (optUser.isPresent()) {
			User user = optUser.get();
			String token = UUID.randomUUID().toString();
			createPasswordResetTokenForUser(user, token);
			javaMailSender.send(constructResetTokenEmail(domainName, request.getLocale(), token, user));
			message = "Password Reset link has been sent to your registered mail ID.";
		} else {
			message = "No user found for the given email";
		}
		
		return ResponseEntity.ok(new ReturnResult(message));
	}

	private void createPasswordResetTokenForUser(User user, String token) {
		PasswordResetToken myToken = user.getPasswordResetToken();
		if(user.getPasswordResetToken()==null) {
		    myToken = new PasswordResetToken(user, token);
		}
		else {
			myToken.setToken(token);
		}
	    passwordResetTokenRepository.save(myToken);
	}
	
	private SimpleMailMessage constructResetTokenEmail(String contextPath, Locale locale, String token, User user) {
		String url = contextPath + "?id=" + user.getId() + "&token=" + token;
		String message = "Hi "+user.getName()+",\r\n\r\nPlease click on the below link to reset your password.\r\n\r\n Considering security of your data, this link will be valid only for the shorter period. So action at the earliest";
		return constructEmail("OMRTB - Reset Password", message + " \r\n\r\n" + url, user);
	}

	private SimpleMailMessage constructRegistrationEmail(Locale locale, User user) {
		String message = "Hi "+user.getName()+",\r\n\r\nThank you for registering OMRTB running group.\r\n\r\nOur core team will review and approve your request. You will get a notification email once approved.\r\n\r\n";
		return constructRegEmail("OMRTB - Sucessfully Registered", message + "\r\n\r\nCheers\r\nOMRTB Team", user);
	}

	private SimpleMailMessage constructRegEmail(String subject, String body, User user) {
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

	private SimpleMailMessage constructEmail(String subject, String body, User user) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject(subject);
		email.setText(body);
		email.setTo(user.getEmail());
		email.setFrom(senderEmail);
		return email;
	}
	
	@GetMapping("/changepassword")
	public ResponseEntity<ChangePasswordResponse> showChangePasswordPage(Locale locale, @RequestParam("id") long id,
			@RequestParam("token") String token) {
		String result = validatePasswordResetToken(id, token);
		ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse();
		if (result != null) {
			changePasswordResponse.setStatus(result);
		}
		else {
			changePasswordResponse.setId(id);
			changePasswordResponse.setToken(token);
			changePasswordResponse.setStatus("Success");
		}
		return ResponseEntity.ok(changePasswordResponse);
	}

	private String validatePasswordResetToken(long id, String token) {
		Optional<PasswordResetToken> optionalPassToken = passwordResetTokenRepository.findByToken(token);
		if (optionalPassToken.isPresent()) {
			PasswordResetToken passToken = optionalPassToken.get();
			if (passToken.getId() != id) {
				return "invalidToken";
			}
			Calendar cal = Calendar.getInstance();
			if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
				return "expired";
			}
			/*User user = passToken.getUser();
			Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
					Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
			SecurityContextHolder.getContext().setAuthentication(auth);*/
			return null;
		} else {
			return "invalidToken";
		}
	}
	@PostMapping("/savepassword")
	@Transactional
	public ResponseEntity<ReturnResult> savePassword(//@AuthenticationPrincipal PdfUserDetails pdfUser,
			@Valid @RequestBody PasswordDto passwordDto) {
		//User user = pdfUser.getUser();
		//final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Optional<PasswordResetToken> optionalPassToken = passwordResetTokenRepository.findByToken(passwordDto.getToken());
		if (optionalPassToken.isPresent()) {
			PasswordResetToken passToken = optionalPassToken.get();
			if (passToken.getId() != passwordDto.getId()) {
				return ResponseEntity.ok(new ReturnResult("invalidToken"));
			}
			Calendar cal = Calendar.getInstance();
			if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
				return ResponseEntity.ok(new ReturnResult("expired"));
			}
			User user = passToken.getUser();
			user.setPasswordResetToken(null);
			passToken.setUser(null);
			String newPassword = passwordDto.getNewPassword();
			String delToken = passToken.getToken();
			passToken = null;
			int deleted = passwordResetTokenRepository.deleteResetToken(delToken);
			changeUserPassword(user, newPassword);
			LOGGER.info("Deleted Record Count :: "+deleted);
			return ResponseEntity.ok(new ReturnResult("Password has been reset successfully"));
			/*User user = passToken.getUser();
			Authentication auth = new UsernamePasswordAuthenticationToken(user, null,
					Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
			SecurityContextHolder.getContext().setAuthentication(auth);
			return null;*/
		} else {
			return ResponseEntity.ok(new ReturnResult("invalidToken"));
		}

	}
	
	private void changeUserPassword(User user, String password) {
	    user.setPassword(encoder.encode(password));
	    userRepository.save(user);
	}

	@Autowired
	private RoleRepository roleRepository;
	
	@GetMapping("/contactus")
	public ResponseEntity<ReturnResult> contactus(@Valid @RequestBody ContactUs contactUs) {
		SimpleMailMessage mail = constructContactUsEmail(contactUs);
		
		javaMailSender.send(mail);
		
		return ResponseEntity.ok(new ReturnResult("Email has been sent to the core team and will get back to you"));
	}
	private SimpleMailMessage constructContactUsEmail(ContactUs contactUs) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject(contactUs.getSubject());
		email.setText(contactUs.getMessage());
		email.setTo(contactUs.getReceiverEmail());
		email.setFrom(contactUs.getSenderEmail());
		email.setCc(contactUs.getSenderEmail());
		return email;
	}
	public ResponseEntity<ReturnResult> importUsers() {
		try {
			//InputStream inputStream = resource.getInputStream();
	        String fileName = "omrtb.csv";
	        ClassLoader classLoader = new RestjpaApplication().getClass().getClassLoader();
	 
			FileReader filereader = new FileReader(classLoader.getResource(fileName).getFile());
	        CSVReader csvReader = new CSVReader(filereader);
	        String[] nextRecord;
	        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	  
	        // we are going to read data line by line 
	        int rec =0;
	        while ((nextRecord = csvReader.readNext()) != null) {
	        	rec++;
	            if(rec!=1 && nextRecord.length==19) {
		            User user = new User();
		            user.setAddress(nextRecord[0]);
		            user.setBloodgroup(nextRecord[2]);
		            user.setCycling("1".equals(nextRecord[4]));
		            user.setDob(new java.sql.Date(df.parse(nextRecord[5]).getTime()));
		            user.setEmail(nextRecord[6]);
		            user.setGender(nextRecord[7]);
		            user.setMobile(nextRecord[8]);
		            user.setName(nextRecord[9]);
		            try {
		            	user.setPincode(new BigDecimal(Integer.parseInt(nextRecord[11])));
		            }
		            catch (Exception e) {
		            	LOGGER.error(", "+nextRecord[11]);
					}
		            user.setTshirt(nextRecord[14]);
		            user.setVenue(nextRecord[17]);
	
		            /////////////
		            
		            try {
		            	User usr = userRepository.save(user);
		            }
		            catch(Exception e) {
		            	LOGGER.error("Unable to insert user "+user);
		            }
	
		            ///////////
	            }
	        }
	        csvReader.close();
	        filereader.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error(e.getLocalizedMessage());
		} catch (ParseException e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getLocalizedMessage());
		}
		return ResponseEntity.ok(new ReturnResult("Imported Please refer mail"));
	}
	public ResponseEntity<ReturnResult> activateMigratedUSers() {
		List<User> userList = userRepository.findMigratedUsers();
		for (User user : userList) {
			activateUser(user);
		}
		return ResponseEntity.ok().body(new ReturnResult("Activated"));
	}
	
	private User activateUser(User user) {
			RoleSingleton roleSingleton = RoleSingleton.getInstance(roleRepository);
			Role role = roleSingleton.getRole("USER");
			//User user = userList.get(0);
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
			return usr;
	}
}
