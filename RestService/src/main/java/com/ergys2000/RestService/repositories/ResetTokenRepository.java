package com.ergys2000.RestService.repositories;


import java.util.Optional;

import com.ergys2000.RestService.models.ResetToken;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/** The repository object for the reset token entity */
public interface ResetTokenRepository extends CrudRepository<ResetToken, Integer> {
	@Query("delete from ResetToken r where r.user.id = ?1")
	@Transactional
	@Modifying
	void deleteByUserId(Integer userId);

	@Query("select r from ResetToken r where r.user.email = ?1")
	Optional<ResetToken> findByEmail(String email);
}
