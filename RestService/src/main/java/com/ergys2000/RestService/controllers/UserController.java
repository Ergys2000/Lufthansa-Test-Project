package com.ergys2000.RestService.controllers;

import java.time.LocalDate;
import java.util.Optional;

import com.ergys2000.RestService.models.ChangePasswordRequest;
import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.RequestRepository;
import com.ergys2000.RestService.repositories.UserRepository;
import com.ergys2000.RestService.services.EmailService;
import com.ergys2000.RestService.util.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

	@Autowired
	PasswordEncoder passwordEncoder;

	@GetMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<Optional<User>> getUser(@PathVariable(name = "id") Integer userId) {
		return new ResponseWrapper<Optional<User>>("OK", userRepository.findById(userId),
				"Users retrieved successfully!");
	}

	@PutMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<User> putUser(@PathVariable(name = "id") Integer userId, @RequestBody User user) {
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

	@PutMapping(path = "/{id}/changepassword")
	@ResponseBody
	public ResponseWrapper<User> changePassword(@PathVariable(name = "id") Integer userId,
			@RequestBody ChangePasswordRequest changePasswordReq) {
		try {
			Optional<User> optionalUser = userRepository.findById(userId);
			if (optionalUser.isEmpty())
				throw new Exception("User does not exist!");
			User user = optionalUser.get();
			if (user.getId() != userId)
				throw new Exception("Sorry you do not have access to update another user!");
			if (!changePasswordReq.getNewPassword().equals(changePasswordReq.getConfirmPassword()))
				throw new Exception("Sorry, new and confirm password do not match!");
			if (!passwordEncoder.matches(changePasswordReq.getOldPassword(), user.getPassword()))
				throw new Exception("Wrong old password!");

			user.setPassword(passwordEncoder.encode(changePasswordReq.getNewPassword()));
			userRepository.save(user);
			return new ResponseWrapper<>("OK", null, "User password updated!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
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
			request.setApproved(null);
			request.setCreatedOn(LocalDate.now());
			if (request.getUser().getId() == userId) {
				Optional<User> user = userRepository.findById(userId);
				if (user.isEmpty()) {
					throw new Exception("Your user could not be found!");
				}
				if (LocalDate.now().isBefore(user.get().getStartDate().plusDays(90))) {
					throw new Exception("Sorry you are still on probation!");
				}
				if (request.getStartDate().isAfter(request.getEndDate()))
					throw new Exception("Sorry, start date cannot be after end date!");

				Optional<User> supervisor = userRepository.findUserSupervisor(userId);

				request = requestRepository.save(request);
				sentEmailToSupervisor(user, supervisor);

				return new ResponseWrapper<Request>("OK", request, "Request added!");
			} else
				throw new Exception("You are not allowed this operation!");
		} catch (Exception e) {
			return new ResponseWrapper<Request>("ERROR", request, e.getMessage());
		}
	}

	private void sentEmailToSupervisor(Optional<User> user, Optional<User> supervisor) {
		String supervisorEmail = supervisor.get().getEmail();
		String subject = "Leave Request Notice";
		String text = user.get().getFirstname() + " " + user.get().getLastname() + " just made a leave request!";

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
