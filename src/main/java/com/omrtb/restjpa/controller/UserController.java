package com.omrtb.restjpa.controller;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.omrtb.restjpa.entity.model.Activity;
import com.omrtb.restjpa.entity.model.ActivityType;
import com.omrtb.restjpa.entity.model.SourceActivity;
import com.omrtb.restjpa.entity.model.StravaUser;
import com.omrtb.restjpa.entity.model.User;
import com.omrtb.restjpa.repository.StravaUserRepository;
import com.omrtb.restjpa.repository.UserRepository;
import com.omrtb.restjpa.request.model.ReqActivity;
import com.omrtb.restjpa.request.model.ReturnResult;
import com.omrtb.restjpa.request.model.UpdateUser;
import com.omrtb.restjpa.utils.StravaUtils;
import com.omrtb.restjpa.utils.Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/authops/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {

	/*@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new UserValidator());
	}*/
	
	private static Logger LOGGER = LogManager.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	//@GetMapping
	public ResponseEntity<Iterable<User>> findAll() {
		return ResponseEntity.ok(userRepository.findAll());
	}

	@Autowired
    private JavaMailSender javaMailSender;
	
	@Autowired
	private StravaUtils stravaUtils;

	@GetMapping("/mail")
	public ResponseEntity<ReturnResult> sendMail() {
		
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("skumarv@hotmail.com");

        msg.setSubject("Testing Mail");
        msg.setText("Hello World \n Test mail message");

        javaMailSender.send(msg);
        return ResponseEntity.ok(new ReturnResult("Refer Console"));
	}

	//@GetMapping("/gender/{gender}")
	public ResponseEntity<List<User>> findByGender(@PathVariable String gender) {
		List<User> user = userRepository.findByGender(gender);

		return ResponseEntity.ok(user);
	}

	//@GetMapping("/email/{email:.+}")
	public ResponseEntity<List<User>> findByEmail(@PathVariable String email) {
		List<User> users = userRepository.findByEmail(email);
		if (users==null || users.isEmpty()) {
			LOGGER.error("Email " + email + " is not existed");
			ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(users);
	}

	//@GetMapping("/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			LOGGER.error("Id " + id + " is not existed");
			ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(user.get());
	}

	@GetMapping("/viewprofile")
	public ResponseEntity<User> viewProfile(@AuthenticationPrincipal PdfUserDetails pdfUser) {
		User user = pdfUser.getUser();
		return ResponseEntity.ok(user);
	}

	@PostMapping("/update")
	public ResponseEntity<User> update(@AuthenticationPrincipal PdfUserDetails pdfUser, @Valid @RequestBody UpdateUser updateUser) {
		
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		//auth.getPrincipal();
		
		User user = pdfUser.getUser();
		updateUser.copyToPersist(user);
		/*if (!userRepository.findById(id).isPresent()) {
			LOGGER.error("Id " + id + " is not existed");
			ResponseEntity.badRequest().build();
		}*/

		return ResponseEntity.ok(userRepository.save(user));
	}

	@GetMapping("/activity")
	public ResponseEntity<Activity> manualEntry(@AuthenticationPrincipal PdfUserDetails pdfUser,  
	  @RequestParam("date") @DateTimeFormat(pattern = "dd-MM-yyyy") String strDate) {
		User user = pdfUser.getUser();
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		//DateTimeFormatter dateTimeFormat = DateTimeFormatter.BASIC_ISO_DATE;
		//LocalDateTime date = formatter.parse(strDate, LocalDateTime::from);
		//LocalDate date = LocalDate.parse(strDate, formatter);
		if (strDate.length() == 10) {
			strDate += " 05:30";
		}
	    //final ZonedDateTime parsed = ZonedDateTime.parse(strDate, formatter.withZone(ZoneId.systemDefault()));
	    //LOGGER.info(parsed.toLocalDateTime());
		
		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyyTHH:mm");
		final DateFormat df =new SimpleDateFormat("dd-MM-yyyy");
		Date dt = null;
		try {
			dt = df.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//date = null;
			e.printStackTrace();
		}
		
		Set<Activity> activities = user.getActivities();
		Activity activity = null;
		final Date date = dt;
		if(activities!=null) {
			Optional<Activity> matchingObject = activities.stream()
												.filter(a -> { 
													String srcData = a.getSourceData();
													ZonedDateTime zoneDate = a.getStartDate();
													ZonedDateTime parsed = Util.convertDateToZonedDate(date);
													parsed = Util.changeZoneID(parsed);
													NumberFormat nf = new DecimalFormat("00");
													Date zonedDt = null;
													Date inputDt = null;
													ZoneId zonedZn = zoneDate.getZone();
													ZoneId inputZn = parsed.getZone();
													try {
														zonedDt = df.parse(nf.format(zoneDate.getDayOfMonth())+"-"+nf.format(zoneDate.getMonthValue())+"-"+nf.format(zoneDate.getYear()));
														inputDt = df.parse(nf.format(parsed.getDayOfMonth())+"-"+nf.format(parsed.getMonthValue())+"-"+nf.format(parsed.getYear()));
													} catch (ParseException e) {
														e.printStackTrace();
													}
													LOGGER.info("zonedZn :: "+zonedZn+", inputZn :: "+inputZn+", "+zonedZn.equals(inputZn)+", Date compare :: "+zonedDt.equals(inputDt));
													//LOGGER.info("zoneDate :: "+zoneDate+", inputZoneDate :: "+parsed);
													//LOGGER.info("Date Compare :: "+zoneDate.equals(parsed));
													//LOGGER.info("Source data Compare :: "+SourceActivity.OMRTB.getDesc().equals(srcData));
													return (SourceActivity.OMRTB.getDesc().equals(srcData) 
																&& zonedDt.equals(inputDt));
												}
														)
												.findFirst();
			if(matchingObject.isPresent()) {
				activity = matchingObject.get();
			}
		}
		return ResponseEntity.ok(activity);
	}

	@PostMapping("/activity/manual")
	public ResponseEntity<ReturnResult> manualEntry(@AuthenticationPrincipal PdfUserDetails pdfUser, @Valid @RequestBody ReqActivity activity) {
		User user = pdfUser.getUser();
		//activity.setStartDate(Util.convertDateToZonedDate(activity.getStartDate()));
		SortedSet<Activity> activities = user.getActivities();
		if(activities==null) {
			activities = new TreeSet<Activity>();
			user.setActivities(activities);
		}
		Optional<Activity> matchingObject = activities.stream()
												.filter(a -> {
													String srcData = a.getSourceData();
													ZonedDateTime zoneDate = a.getStartDate();
													ZonedDateTime inputZoneDate = Util.convertDateToZonedDate(activity.getStartDate());
													inputZoneDate = Util.changeZoneID(inputZoneDate);
													final DateFormat df =new SimpleDateFormat("dd-MM-yyyy");
													NumberFormat nf = new DecimalFormat("00");
													Date zonedDt = null;
													Date inputDt = null;
													ZoneId zonedZn = zoneDate.getZone();
													ZoneId inputZn = inputZoneDate.getZone();
													try {
														zonedDt = df.parse(nf.format(zoneDate.getDayOfMonth())+"-"+nf.format(zoneDate.getMonthValue())+"-"+nf.format(zoneDate.getYear()));
														inputDt = df.parse(nf.format(inputZoneDate.getDayOfMonth())+"-"+nf.format(inputZoneDate.getMonthValue())+"-"+nf.format(inputZoneDate.getYear()));
													} catch (ParseException e) {
														e.printStackTrace();
													}
													LOGGER.info("zonedZn :: "+zonedZn+", inputZn :: "+inputZn+", "+zonedZn.equals(inputZn)+", Date compare :: "+zonedDt.equals(inputDt));
													//LOGGER.info("zoneDate :: "+df.format(zonedDt)+", inputDate :: "+df.format(inputDt));
													//LOGGER.info("Date Compare :: "+zoneDate.equals(inputZoneDate));
													//LOGGER.info("Source data Compare :: "+SourceActivity.OMRTB.getDesc().equals(srcData));
													return (SourceActivity.OMRTB.getDesc().equals(srcData) 
																&& zonedDt.equals(inputDt));
												}).findFirst();
		Activity tempActivity;
		if(matchingObject.isPresent()) {
			LOGGER.info("Activity matches - just update");
			tempActivity = matchingObject.get();
		}
		else {
			LOGGER.info("No Activity - just insert");
			tempActivity = new Activity();
			tempActivity.setSourceData(SourceActivity.OMRTB.getDesc());
			tempActivity.setStartDate(Util.convertDateToZonedDate(activity.getStartDate()));
			tempActivity.setStartDateLocal(Util.convertZonedDateToLocalDate(tempActivity.getStartDate()));
			tempActivity.setUser(user);
		}
		tempActivity.setMovingTime(activity.getMovingTime());
		tempActivity.setElapsedTime(activity.getElapsedTime());
		//tempActivity.setMovingTime(Util.secondsToHoursMinutesSecondsConverter(activity.getMovingTime()));
		//tempActivity.setElapsedTime(Util.secondsToHoursMinutesSecondsConverter(activity.getElapsedTime()));
		tempActivity.setDistance(activity.getDistance());
		//tempActivity.setActivityType(activity.getActivityType());
		tempActivity.setWorkoutName(activity.getWorkoutName());
		tempActivity.setWorkoutType(activity.getWorkoutType());
		tempActivity.setActivityType(ActivityType.get(activity.getActivityType()));
		activities.add(tempActivity);
		User entity = userRepository.save(user);
		ResponseEntity<ReturnResult> response = null;
		if(entity!=null) {
			response = ResponseEntity.ok(new ReturnResult("Success"));
		}
		else {
			response = ResponseEntity.ok(new ReturnResult("Fail"));
		}
		return response;
	}

	@PostMapping("/stravapost")
	public ResponseEntity<User> updateStrava(@AuthenticationPrincipal PdfUserDetails pdfUser, @RequestBody StravaUser stravaUser) {
		User user = pdfUser.getUser();
		if(user.getStravaUser()==null) {
			user.setStravaUser(stravaUser);
			stravaUser.setUser(user);
		}
		else {
			StravaUser stravaUserFrmUsr = user.getStravaUser();
			stravaUserFrmUsr.copy(stravaUser);
		}
		return ResponseEntity.ok(userRepository.save(user));
	}

	@GetMapping("/stravadetails")
	public ResponseEntity<StravaUser> getStravaDetails(@AuthenticationPrincipal PdfUserDetails pdfUser) {
		User user = pdfUser.getUser();
		StravaUser stravaUserFrmUsr = user.getStravaUser();
		return ResponseEntity.ok(stravaUserFrmUsr);
	}

	@GetMapping("/strava")
	public ResponseEntity<StravaUser> updateStrava(@AuthenticationPrincipal PdfUserDetails pdfUser, 
									@RequestParam("state") String state, 
									@RequestParam("code") String code, 
									@RequestParam("scope") String scope) {
		return updateStravaDtl(pdfUser, state, code, scope, null);
	}
	
	@GetMapping("/stravaautherror")
	public ResponseEntity<StravaUser> updateStrava(@AuthenticationPrincipal PdfUserDetails pdfUser, 
									@RequestParam("state") String state, 
									@RequestParam("code") String code, 
									@RequestParam("scope") String scope, 
									@RequestParam("error") String error) {
		return updateStravaDtl(pdfUser, state, code, scope, error);
	}
	
	@GetMapping("/stravaerror")
	public ResponseEntity<StravaUser> updateStrava(@AuthenticationPrincipal PdfUserDetails pdfUser, 
									@RequestParam("error") String error) {
		return updateStravaDtl(pdfUser, null, null, null, error);
	}

	@Autowired
	private StravaUserRepository stravaUserRepository;
	
	private ResponseEntity<StravaUser> updateStravaDtl(PdfUserDetails pdfUser, 
									String state, 
									String code, 
									String scope, 
									String error) {
		
		User user = pdfUser.getUser();
		if(user.getStravaUser()==null) {
			StravaUser stravaUser = new StravaUser();
			stravaUser.setCode(code);
			stravaUser.setState(state);
			stravaUser.setScope(scope);
			stravaUser.setError(error);
			user.setStravaUser(stravaUser);
			stravaUser.setUser(user);
		}
		else {
			StravaUser stravaUserFrmUsr = user.getStravaUser();
			stravaUserFrmUsr.setCode(code);
			stravaUserFrmUsr.setState(state);
			stravaUserFrmUsr.setScope(scope);
			stravaUserFrmUsr.setError(error);
		}
		StravaUser strUsr = user.getStravaUser();
		stravaUtils.authorizeStravaUsersOnDemand(strUsr);
		User entity = null;
		Long count = stravaUserRepository.findAthlete(strUsr.getUser().getId(), strUsr.getAtheleteId());
		StravaUser stravaUser = null;
		LOGGER.error("Is the strava user exists :: "+count);
		if(count==null || count.equals(0l)) {
			entity = userRepository.save(user);
			if(entity!=null) {
				entity.getStravaUser().setAuthorizationStatus("Authorized Successfully");
			}
			else {
				stravaUser = new StravaUser();
				stravaUser.setAuthorizationStatus("There is some technical challenge in linking your strava account, please contact the admin team regarding this");
			}
		}
		else {
			stravaUser = new StravaUser();
			stravaUser.setAuthorizationStatus("The strava account you are trying to link is already linked another user");
		}
		
		ResponseEntity<StravaUser> response = null;
		if(entity!=null) {
			response = ResponseEntity.ok(entity.getStravaUser());
		}
		else {
			LOGGER.error("StravaUser Obj :: "+stravaUser);
			response = ResponseEntity.ok(stravaUser);
		}
		return response;
	}

	@DeleteMapping("/strava/delete")
	public ResponseEntity<StravaUser> deleteStrava(@AuthenticationPrincipal PdfUserDetails pdfUser) {
		User user = pdfUser.getUser();

		StravaUser strUsr = user.getStravaUser();
		if(strUsr!=null) {
			strUsr.setUser(null);
		}
		user.setStravaUser(null);
		userRepository.deleteStravUser(strUsr.getId());

		return ResponseEntity.ok().build();
	}
	
	//@DeleteMapping("/{id}")
	public ResponseEntity<User> delete(@AuthenticationPrincipal PdfUserDetails pdfUser) {

		userRepository.deleteById(pdfUser.getUser().getId());

		return ResponseEntity.ok().build();
	}
	@GetMapping("/listallactivities")
	public ResponseEntity<Set<Activity>> listAllUserActivities(@AuthenticationPrincipal PdfUserDetails pdfUser) {
		User user = pdfUser.getUser();
		Optional<User> optionalUser = userRepository.findUniqueUserByEmail(user.getEmail());
		if(optionalUser.isPresent()) {
			user = optionalUser.get();
		}
		Set<Activity> activities = user.getActivities();
		for (Activity activity : activities) {
			LOGGER.error(activity);
		}
		return ResponseEntity.ok(activities);
	}
}