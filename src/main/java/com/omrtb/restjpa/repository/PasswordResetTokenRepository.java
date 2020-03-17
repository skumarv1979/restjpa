package com.omrtb.restjpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.omrtb.restjpa.entity.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

	@Query("FROM PasswordResetToken WHERE token=?1")
	Optional<PasswordResetToken> findByToken(String token);
	
	@Query("delete from PasswordResetToken where token=?1")
	@Modifying
	int deleteResetToken(String token);
}
