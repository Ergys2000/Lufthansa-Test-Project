package com.ergys2000.RestService.repositories;



import java.util.Optional;

import com.ergys2000.RestService.models.User;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends CrudRepository<User, Integer> {
	@Query("select u from User u where u.email = ?1")
	Optional<User> findByEmail(String email);

	@Query("select u from User u where u.type = ?1")
	Iterable<User> findByType(String type);

	@Query("select u from User u where u.supervisor.id = ?1")
	Iterable<User> findBySupervisorId(Integer supervisorId);
	
	@Query("select u.supervisor from User u where u.id = ?1")
	Optional<User> findUserSupervisor(Integer userId);

	@Query("delete from User u where u.id = ?1 and u.type = 'user'")
	@Transactional
	@Modifying
	void deleteUserById(Integer id);
}
