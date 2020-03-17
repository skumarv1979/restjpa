package com.omrtb.restjpa.repository;

import org.springframework.data.repository.CrudRepository;

import com.omrtb.restjpa.entity.model.Activity;

public interface ActivityRepository extends CrudRepository<Activity, Long> {

	//@Query("SELECT a FROM Activity a JOIN a.User u WHERE u.id=?1")
    //List<Activity> listUserActivities(Long id);
}
