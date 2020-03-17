package com.omrtb.restjpa.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.omrtb.restjpa.utils.StravaUtils;

@Component
public class BatchProcessor {

	@Value("#{'${strava.client_id}'}")
	private String clientId;

	@Value("#{'${strava.client_secret}'}")
	private String clientSecret;
	
	@Autowired
	private StravaUtils stravaUtils;

	private static Logger LOGGER = LogManager.getLogger(BatchProcessor.class);

	//@Scheduled(fixedRate = 10000)//(cron = "0 * 9 * * ?")  //*/10 * * * * *   //0 0 */2 * * *
	@Scheduled(cron = "0 0 */1 * * *",zone = "Asia/Kolkata")
	public void cronJobSch() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date now = new Date();
		String strDate = sdf.format(now);
		LOGGER.info("starting to sync Strava activity "+strDate);
		stravaUtils.updateStravaActivity();
		LOGGER.info("Syncing Strava activity done at "+sdf.format(new Date()));
		//sendEmail();
		//LOGGER.info("Java cron job expression:: " + strDate);
		
		
	}
	@Autowired
    private JavaMailSender javaMailSender;
	
	public void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("skumarv@hotmail.com");

        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");

        javaMailSender.send(msg);

    }
}
