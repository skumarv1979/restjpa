package com.omrtb.restjpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.omrtb.restjpa.entity.model.StravaUser;

@RepositoryRestResource
public interface StravaUserRepository extends CrudRepository<StravaUser, Long> {

    @Query("FROM StravaUser WHERE code is not null and error is null")
    List<StravaUser> listAllStravaUsers();

    @Query("SELECT COUNT(su) FROM StravaUser su WHERE su.id!=?1 and su.atheleteId=?2 ")
    Long findAthlete(Long id, Integer atheleteId);

}
