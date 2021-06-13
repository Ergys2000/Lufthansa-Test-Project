package com.ergys2000.RestService.repositories;


import com.ergys2000.RestService.models.Request;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<Request, Integer> {
	@Query("select r from Request r where r.user.id = ?1")
	Iterable<Request> findByUserId(Integer userId);

	@Query("select r from Request r where r.user.supervisor.id = ?1")
	Iterable<Request> findBySupervisorId(Integer supervisorId);
}
