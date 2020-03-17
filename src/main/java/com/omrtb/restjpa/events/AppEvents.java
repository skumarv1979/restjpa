package com.omrtb.restjpa.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.omrtb.restjpa.repository.RoleRepository;
import com.omrtb.restjpa.utils.RoleSingleton;

@Component
public class AppEvents {
	
	@Autowired
	private RoleRepository roleRepository;
	
	private static Logger LOGGER = LogManager.getLogger(AppEvents.class);

    //@EventListener(ApplicationContextInitializedEvent.class)
	//@EventListener(ApplicationEvent.class)
	@EventListener(ApplicationReadyEvent.class)
    public void startApp() {
    	//stravaUtils.authorizeStravaUsers();
		LOGGER.info("Do any start up tasks here");
		RoleSingleton.getInstance(roleRepository);
    }
}
