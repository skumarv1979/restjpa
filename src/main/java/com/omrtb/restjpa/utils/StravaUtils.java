package com.omrtb.restjpa.utils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.omrtb.restjpa.entity.model.Activity;
import com.omrtb.restjpa.entity.model.ActivityType;
import com.omrtb.restjpa.entity.model.SourceActivity;
import com.omrtb.restjpa.entity.model.StravaUser;
import com.omrtb.restjpa.entity.model.User;
import com.omrtb.restjpa.repository.StravaUserRepository;
import com.omrtb.restjpa.repository.UserRepository;
import com.omrtb.restjpa.strava.MyStravaAPI;
import com.omrtb.restjpa.strava.MyStravaAuthorisationAPI;
import com.omrtb.restjpa.strava.MyStravaRefreshTokenAPI;
import com.omrtb.restjpa.strava.MyStravaTokenResponse;

import javastrava.auth.model.Token;
import javastrava.model.StravaActivity;
import javastrava.service.ActivityService;

@Component
public class StravaUtils {

	@Value("#{'${strava.client_id}'}")
	private Integer clientId;

	@Value("#{'${strava.client_secret}'}")
	private String clientSecret;
	
	private static Logger LOGGER = LogManager.getLogger(StravaUtils.class);

	@Autowired
	private StravaUserRepository stravaUserRepository;
	
	@Autowired
	private UserRepository userRepository;

	private Map<Long, Token> authTokens = new HashMap<Long, Token>();
	
	public StravaUser updateAuthToken(StravaUser stravaUser, MyStravaTokenResponse response) {
		Token token = new Token(response);
		Long userId = stravaUser.getUser().getId();
		authTokens.remove(userId);
		authTokens.put(userId, token);
		stravaUser.setAtheleteId(response.getAthlete().getId());
		stravaUser.setProfile(response.getAthlete().getProfile());
		stravaUser.setProfileMedium(response.getAthlete().getProfileMedium());
		stravaUser.setAccessToken(response.getAccessToken());
		stravaUser.setRefreshToken(response.getRefreshToken());
		stravaUser.setExpiresAt(new java.sql.Date((new Date(response.getExpiresAt())).getTime()));
		stravaUser.setExpiresIn(response.getExpiresIn());
		return stravaUser;
	}
	
	public Token getToken(User user) {
		StravaUser su = user.getStravaUser();
		String refreshToken = su.getRefreshToken();
		MyStravaRefreshTokenAPI refersh = MyStravaAPI.refreshAuthorisationInstance();
		MyStravaTokenResponse response = refersh.tokenExchangeMyStravaRefresh(clientId, clientSecret, Constants.REFRESH_TOKEN, refreshToken);
		su.setAccessToken(response.getAccessToken());
		su.setRefreshToken(response.getRefreshToken());
		su.setExpiresAt(new java.sql.Date((new Date(response.getExpiresAt())).getTime()));
		su.setExpiresIn(response.getExpiresIn());
		stravaUserRepository.save(su);
		Token token = new Token(response);
		return token;
	}
	
	public void authorizeStravaUsers() {
		List<StravaUser> stravaUsers = stravaUserRepository.listAllStravaUsers();
		for (StravaUser stravaUser : stravaUsers) {
			stravaAuthAndUpdate(stravaUser);
		}
	}
	
	public StravaUser authorizeStravaUsersOnDemand(StravaUser stravaUser) {
		if(stravaUser==null || Strings.isNullOrEmpty(stravaUser.getCode()) ) {
			authTokens.remove(stravaUser.getUser().getId());
			return null; //TODO
		}
		else {
			return stravaAuthAndUpdate(stravaUser);
		}
	}
	
	public StravaUser stravaAuthAndUpdate(StravaUser stravaUser) {
		MyStravaTokenResponse response = stravaAuthorize(stravaUser);
		return updateAuthToken(stravaUser, response);
	}
	
	public MyStravaTokenResponse stravaAuthorize(StravaUser stravaUser) {
		LOGGER.info("stravaUser.getCode() :: "+stravaUser.getCode()+", "+stravaUser.getId());
		MyStravaAuthorisationAPI auth = MyStravaAPI.myAuthorisationInstance();
		MyStravaTokenResponse response = auth.tokenExchange(clientId, clientSecret, stravaUser.getCode(), Constants.AUTHORIZATION_CODE);
		return response;
	}
	public void updateStravaActivity() {
		LOGGER.info("Update Activity called");
		List<User> users = userRepository.pullStravaUsers();
		for (User user : users) {
			StravaUser stravaUser = user.getStravaUser();
			if(stravaUser!=null && !Strings.isNullOrEmpty(stravaUser.getCode()) && !Strings.isNullOrEmpty(stravaUser.getRefreshToken())) {
				serviceCall(user);
			}
		}
		/*List<StravaUser> stravaUsers = stravaUserRepository.listAllStravaUsers();
		for (StravaUser stravaUser : stravaUsers) {
			LOGGER.info("We have some users ");
			User user = stravaUser.getUser();
			//updateActivity(stravaUser);
			serviceCall(user);
		}*/
	}
	public List<StravaActivity> serviceCall(User user) {
		LocalDateTime before = LocalDateTime.now();
		LocalDateTime after = LocalDateTime.now().minusDays(7);
		//Paging page = new Paging(0,20);
		Token token = getToken(user);
		ActivityService sa = token.getService(ActivityService.class);
		List<StravaActivity>  ll = sa.listAuthenticatedAthleteActivities(before, after);//, page
		LOGGER.info("Looging data for the user -------"+user.getEmail());
		SortedSet<Activity> activities = user.getActivities();
		if(activities==null) {
			activities = new TreeSet<Activity>();
			user.setActivities(activities);
		}

		for (StravaActivity stravaActivity : ll) {
			//LOGGER.info("What is :: "+stravaActivity.getId()+", "+stravaActivity.getName()+",  "+stravaActivity.getWorkoutType().getDescription()+", "+stravaActivity.getElapsedTime()/60 + ", "+stravaActivity.getStartDateLocal()+", "+stravaActivity.getType().getDescription());
			
			Optional<Activity> matchingObject = activities.stream().
		    filter(a -> (a.getSourceData().equals(SourceActivity.STRAVA.getDesc()) && a.getActivityId().equals(stravaActivity.getId()))).
		    findFirst();
			Activity activity = null;
			if(matchingObject.isPresent()) {
				activity = matchingObject.get();
			}
			else {
				activity = new Activity();
				activity.setUser(user);
			}
			
			activity.setActivityId(stravaActivity.getId());
			activity.setSourceData(SourceActivity.STRAVA.getDesc());
			activity.setCommute(stravaActivity.getCommute());
			activity.setStartDate(stravaActivity.getStartDate());
			activity.setDistance(stravaActivity.getDistance());
			activity.setCalories(stravaActivity.getCalories());
			activity.setMovingTime(stravaActivity.getMovingTime());
			activity.setElapsedTime(stravaActivity.getElapsedTime());
			//activity.setMovingTime(Util.secondsToHoursMinutesSecondsConverter(stravaActivity.getMovingTime()));
			//activity.setElapsedTime(Util.secondsToHoursMinutesSecondsConverter(stravaActivity.getElapsedTime()));
			activity.setManual(stravaActivity.getManual());
			activity.setStartDate(stravaActivity.getStartDate());
			activity.setStartDateLocal(stravaActivity.getStartDateLocal());
			activity.setTimezone(stravaActivity.getTimezone());
			activity.setTotalElevationGain(stravaActivity.getTotalElevationGain());
			activity.setActivityType(stravaActivity.getType()!=null?ActivityType.get(stravaActivity.getType().getDescription()):null);
			activity.setWorkoutName(stravaActivity.getName());
			activity.setWorkoutType(stravaActivity.getWorkoutType()!=null?stravaActivity.getWorkoutType().getDescription():null);
			activities.add(activity);
		}
		userRepository.save(user);
		LOGGER.info("Abve is the data for the past 7 days -------"+user.getEmail());
		return ll;
	}
}
