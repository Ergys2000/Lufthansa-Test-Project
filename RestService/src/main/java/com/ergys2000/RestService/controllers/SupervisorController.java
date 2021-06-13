
package com.ergys2000.RestService.controllers;

import java.util.Optional;

import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.RequestRepository;
import com.ergys2000.RestService.repositories.UserRepository;
import com.ergys2000.RestService.services.EmailService;
import com.ergys2000.RestService.util.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/supervisor")
public class SupervisorController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RequestRepository requestRepository;
	@Autowired
	private EmailService emailService;

	@GetMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<Optional<User>> getUser(@PathVariable(name = "id") Integer userId) {
		try {
			Optional<User> user = userRepository.findById(userId);

			if (user.isEmpty())
				throw new Exception("User does not exist!");
			if (!user.get().getType().equals("supervisor"))
				throw new Exception("You are not a supervisor!");

			return new ResponseWrapper<Optional<User>>("OK", user, "Users retrieved successfully!");

		} catch (Exception e) {
			return new ResponseWrapper<Optional<User>>("OK", null, e.getMessage());
		}
	}

	@PutMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<User> postUser(@PathVariable(name = "id") Integer userId, @RequestBody User user) {
		try {
			if (user.getSupervisor() != null)
				throw new Exception("A supervisor cannot have another supervisor!");

			user.setId(userId);
			user = userRepository.save(user);
			return new ResponseWrapper<>("OK", user, "User saved!");
		} catch (Exception e) {
			return new ResponseWrapper<>("OK", user, "User saved!");
		}
	}

	@GetMapping(path = "/{id}/requests")
	@ResponseBody
	public ResponseWrapper<Iterable<Request>> getAllRequests(@PathVariable(name = "id") Integer userId) {
		return new ResponseWrapper<Iterable<Request>>("OK", requestRepository.findBySupervisorId(userId), "");
	}

	@PutMapping(path = "/{id}/requests/{requestId}")
	@ResponseBody
	public ResponseWrapper<Request> postRequest(@RequestParam Boolean approved,
			@PathVariable(name = "id") Integer userId, @PathVariable(name = "requestId") Integer requestId) {
		try {
			Optional<Request> request = requestRepository.findById(requestId);
			if (request.isEmpty())
				throw new Exception("Could not find that request!");
			if (request.get().getUser().getSupervisor().getId() != userId)
				throw new Exception("You do not have permission to modify this request!");

			sendEmailToUser(approved, request);
			Request req = request.get();

			req.setApproved(approved);
			req = requestRepository.save(req);
			return new ResponseWrapper<Request>("OK", req, "Request updated!");
		} catch (Exception e) {
			return new ResponseWrapper<Request>("ERROR", null, e.getMessage());
		}
	}

	private void sendEmailToUser(Boolean approved, Optional<Request> request) {
		User user = request.get().getUser();
		String userEmail = user.getEmail();
		String subject = "Leave Request Update";
		String text = null;
		if (approved) {
			text = "One of your requests just got approved! Log into the system to check it.";
		} else {
			text = "One of your requests got rejected! Log into the system to check it.";
		}
		emailService.sendSimpleMessage(userEmail, subject, text);
	}
}
