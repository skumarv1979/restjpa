package com.omrtb.restjpa.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.omrtb.restjpa.entity.model.User;

@RepositoryRestResource
public interface UserRepository extends CrudRepository<User, Long> {
    @Query("FROM User WHERE gender = ?1 order by dob ASC")
    List<User> findByGender(String gender);

    @Query("FROM User WHERE user_id = ?1 order by dob ASC")
    Optional<User> findByUserId(String userId);

    @Query("FROM User WHERE email = ?1 and status=2")
    Optional<User> findUniqueUserByEmail(String userId);

    @Query("FROM User WHERE mobile = ?1 and status=2 order by dob ASC")
    List<User> findByMobile(String mobile);

    @Query("FROM User WHERE email = ?1 order by dob ASC")
    List<User> findByEmail(String email);

    @Query("FROM User WHERE password is null order by dob ASC")
    List<User> findMigratedUsers();

    @Transactional
    @Modifying
    @Query("Delete FROM StravaUser su WHERE su.id = ?1")
    void deleteStravUser(Long id);
    
    @Transactional
    @Query("SELECT u FROM User u, StravaUser su where u.id=su.id")
    List<User> pullStravaUsers();

    @Query("FROM User WHERE status is null or status=1 order by createdAt ASC")
    List<User> findAllNewUsers();

    @Query("SELECT COUNT(u) FROM User u, StravaUser su where u.id=su.id and u.id!=?1 and su.atheleteId=?2")
    Long findAthlete(Long id, Integer atheleteId);

}