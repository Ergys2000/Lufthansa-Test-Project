package com.ergys2000.RestService.repositories;


import com.ergys2000.RestService.models.Request;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/** The repository object for the request entity */
public interface RequestRepository extends CrudRepository<Request, Integer> {
	@Query("select r from Request r where r.user.id = ?1 order by r.id desc")
	Iterable<Request> findByUserId(Integer userId);

	@Query("select r from Request r where r.user.supervisor.id = ?1 order by r.id desc")
	Iterable<Request> findBySupervisorId(Integer supervisorId);
}
