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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RequestRepository requestRepository;
	@Autowired
	private EmailService emailService;

	@GetMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<Optional<User>> getUser(@PathVariable(name = "id") Integer userId) {
		return new ResponseWrapper<Optional<User>>("OK", userRepository.findById(userId),
				"Users retrieved successfully!");
	}

	@PutMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<User> postUser(@PathVariable(name = "id") Integer userId, @RequestBody User user) {
		try {
			if (user.getId() != userId) {
				throw new Exception("Sorry you do not have access to update another user!");
			}
			user.setId(userId);
			return new ResponseWrapper<>("OK", userRepository.save(user), "User saved!");
		} catch (Exception e) {
			return new ResponseWrapper<>("OK", user, e.getMessage());
		}
	}

	@GetMapping(path = "/{id}/requests")
	@ResponseBody
	public ResponseWrapper<Iterable<Request>> getAllRequests(@PathVariable(name = "id") Integer userId) {
		return new ResponseWrapper<Iterable<Request>>("OK", requestRepository.findByUserId(userId), "");
	}

	@PostMapping(path = "/{id}/requests")
	@ResponseBody
	public ResponseWrapper<Request> postRequest(@RequestBody Request request,
			@PathVariable(name = "id") Integer userId) {
		try {
			if (request.getUser().getId() == userId) {

				request = requestRepository.save(request);

				Optional<User> user = userRepository.findById(userId);
				Optional<User> supervisor = userRepository.findUserSupervisor(userId);

				sentEmailToSupervisor(user, supervisor);

				return new ResponseWrapper<Request>("OK", request, "Request added!");
			} else
				throw new Exception("You are not allowed this operation!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper<Request>("ERROR", request, e.getMessage());
		}
	}

	private void sentEmailToSupervisor(Optional<User> user, Optional<User> supervisor) {
		String supervisorEmail = supervisor.get().getEmail();
		String subject = "Leave Request Notice";
		String text = user.get().getFirstname() + " " + user.get().getLastname()
				+ " just made a leave request!";

		emailService.sendSimpleMessage(supervisorEmail, subject, text);
	}

	@DeleteMapping(path = "/{id}/requests/{requestId}")
	@ResponseBody
	public ResponseWrapper<Object> deleteRequest(@PathVariable(name = "requestId") Integer requestId,
			@PathVariable(name = "id") Integer userId) {

		try {
			Optional<Request> request = requestRepository.findById(requestId);
			if (request.isEmpty()) {
				throw new Exception("The request could not be found");
			}
			if (request.get().getUser().getId() != userId) {
				throw new Exception("You are not allowed this operation!");
			}
			requestRepository.deleteById(requestId);
			return new ResponseWrapper<Object>("OK", null, "Request deleted!");
		} catch (Exception e) {
			return new ResponseWrapper<Object>("ERROR", null, e.getMessage());
		}
	}

}
