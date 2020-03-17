package com.omrtb.restjpa.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omrtb.restjpa.entity.model.Events;
import com.omrtb.restjpa.entity.model.User;
import com.omrtb.restjpa.repository.EventsRepository;
import com.omrtb.restjpa.repository.UserRepository;
import com.omrtb.restjpa.request.model.ResponseEvent;
import com.omrtb.restjpa.request.model.ReturnResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/authops/events")
@Slf4j
@RequiredArgsConstructor
public class EventsController {

	/*@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(new EventsValidator());
	}*/
	private static Logger LOGGER = LogManager.getLogger(EventsController.class);

	@Autowired
	private EventsRepository eventsRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/listallopenevents")
	public ResponseEntity<List<ResponseEvent>> findAllOpenEvents(@AuthenticationPrincipal PdfUserDetails pdfUser) {
		List<Events> events = eventsRepository.findAllOpenEvents();
		User user = pdfUser.getUser();
		Set<Events> userEvents = user.getEvents();
		List<ResponseEvent> responseEvents = new ArrayList<ResponseEvent>();

		for (Events event : events) {
			ResponseEvent respEvent = new ResponseEvent();
			respEvent.copyEventEntityToRepsonse(event, (userEvents!=null && userEvents.contains(event)));
			responseEvents.add(respEvent);
		}
		return ResponseEntity.ok(responseEvents);
	}

	@PostMapping(("/register/id/{id}"))
	public ResponseEntity<ReturnResult> registerById(@AuthenticationPrincipal PdfUserDetails pdfUser, @PathVariable Long id) {
		User user = pdfUser.getUser();
		ResponseEntity<ReturnResult> resp = null;
		Optional<Events> eventsOp = eventsRepository.findById(id);
		Set<Events> usersEvents = user.getEvents();
		if (!eventsOp.isPresent()) {
			LOGGER.error("Event Id " + id + " is not existed");
			resp = ResponseEntity.badRequest().body(new ReturnResult("Event Id " + id + " is not existed"));
		}
		else if(usersEvents!=null && usersEvents.contains(eventsOp.get())) {
			LOGGER.error("Already registered for the event");
			resp = ResponseEntity.badRequest().body(new ReturnResult("Already registered for the event"));
		}
		else {
			Events events = eventsOp.get();
			Set<Events> usrEvents = user.getEvents();
			if(usrEvents==null) {
				usrEvents = new HashSet<Events>();
				user.setEvents(usrEvents);
			}
			usrEvents.add(events);
			Set<User> eventSubsUsers = events.getUsers();
			if(eventSubsUsers==null) {
				eventSubsUsers = new HashSet<User>();
				events.setUsers(eventSubsUsers);
			}
			eventSubsUsers.add(user);
			userRepository.save(user);
			resp = ResponseEntity.ok(new ReturnResult("Succesfully registered"));
		}
		return resp;
	}

	@PostMapping(("/register/name/{name}"))
	public ResponseEntity<ReturnResult> registerByName(@AuthenticationPrincipal PdfUserDetails pdfUser, @PathVariable String name) {
		User user = pdfUser.getUser();
		ResponseEntity<ReturnResult> resp = null;
		List<Events> events = eventsRepository.findByName(name);
		Set<Events> usersEvents = user.getEvents();
		if (events == null || events.isEmpty()) {
			LOGGER.error("Event Name " + name + " is not existed");
			resp = ResponseEntity.badRequest().body(new ReturnResult("Event Name " + name + " is not existed"));
		}
		else if(events.size()>1) {
			LOGGER.error("More than one event exists Event Name " + name);
			resp = ResponseEntity.badRequest().body(new ReturnResult("More than one event exists Event Name " + name));
		}
		else if(usersEvents!=null && usersEvents.contains(events.get(0))) {
			LOGGER.error("Already registered for the event");
			resp = ResponseEntity.badRequest().body(new ReturnResult("Already registered for the event"));
		}
		else {
			Events event = events.get(0);
			Set<Events> usrEvents = user.getEvents();
			if(usrEvents==null) {
				usrEvents = new HashSet<Events>();
				user.setEvents(usrEvents);
			}
			usrEvents.add(event);
			Set<User> eventRegUsers = event.getUsers();
			if(eventRegUsers==null) {
				eventRegUsers = new HashSet<User>();
				event.setUsers(eventRegUsers);
			}
			eventRegUsers.add(user);
			userRepository.save(user);
			resp = ResponseEntity.ok(new ReturnResult("Succesfully registered"));
		}
		return resp;
	}

	@PostMapping("/{id}")
	public ResponseEntity<Events> update(@PathVariable Long id, @Valid @RequestBody Events events) {
		if (!eventsRepository.findById(id).isPresent()) {
			LOGGER.error("Id " + id + " is not existed");
			ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(eventsRepository.save(events));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Events> delete(@PathVariable Long id) {
		if (!eventsRepository.findById(id).isPresent()) {
			LOGGER.error("Id " + id + " is not existed");
			ResponseEntity.badRequest().build();
		}

		eventsRepository.deleteById(id);

		return ResponseEntity.ok().build();
	}
}
