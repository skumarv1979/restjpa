package com.omrtb.restjpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.omrtb.restjpa.entity.model.Events;

@RepositoryRestResource
public interface EventsRepository extends CrudRepository<Events, Long> {

    @Query("FROM Events WHERE status = 'Open' order by start_date desc")
    List<Events> findAllOpenEvents();

    @Query("FROM Events WHERE name = ?1 and status='Open' order by start_date desc")
    List<Events> findByName(String name);
}
