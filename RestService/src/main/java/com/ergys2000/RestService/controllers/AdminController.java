package com.ergys2000.RestService.controllers;

import java.util.Optional;

import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.RequestRepository;
import com.ergys2000.RestService.repositories.UserRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/{adminId}")
public class AdminController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RequestRepository requestRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping(path = "")
	@ResponseBody
	public ResponseWrapper<Optional<User>> getAdmin(@PathVariable(name = "adminId") Integer adminId) {
		return new ResponseWrapper<Optional<User>>("OK", userRepository.findById(adminId),
				"Users retrieved successfully!");
	}

	@PutMapping(path = "")
	@ResponseBody
	public ResponseWrapper<User> postUser(@PathVariable(name = "adminId") Integer adminId, @RequestBody User user) {
		try {
			if (user.getSupervisor() != null)
				throw new Exception("An admin cannot have a supervisor!");

			user.setId(adminId);
			user = userRepository.save(user);
			return new ResponseWrapper<>("OK", user, "User saved!");
		} catch (Exception e) {
			return new ResponseWrapper<>("OK", user, "User saved!");
		}
	}

	@GetMapping(path = "/users")
	@ResponseBody
	public ResponseWrapper<Iterable<User>> getAllUsers() {
		return new ResponseWrapper<Iterable<User>>("OK", userRepository.findAll(), "Users retrieved successfully!");
	}

	@PostMapping(path = "/users")
	@ResponseBody
	public ResponseWrapper<User> postUser(@RequestBody User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);
		return new ResponseWrapper<>("OK", savedUser, "User added!");
	}

	@PutMapping(path = "/users/{userId}")
	@ResponseBody
	public ResponseWrapper<Object> putUser(@PathVariable(name = "userId") Integer userId, @RequestBody User newUser) {
		try {
			Optional<User> u = userRepository.findById(userId);
			if (u.isEmpty())
				throw new Exception("The user id does not exist.");

			User user = u.get();
			user.setType(newUser.getType());
			user.setEmail(newUser.getEmail());
			user.setStartDate(newUser.getStartDate());
			user.setFirstname(newUser.getFirstname());
			user.setLastname(newUser.getLastname());
			user.setSupervisor(newUser.getSupervisor());

			if (user.getSupervisor() != null) {
				if (user.getId().equals(user.getSupervisor().getId()))
					throw new Exception("Sorry, the user cannot have himself as supervisor!");
			}

			userRepository.save(user);
			return new ResponseWrapper<>("OK", user, "User updated!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper<>("ERROR", newUser, e.getMessage());
		}
	}

	@DeleteMapping(path = "/users/{userId}")
	@ResponseBody
	public ResponseWrapper<Object> deleteUser(@PathVariable(name = "userId") Integer userId) {
		try {
			userRepository.deleteUserById(userId);
			return new ResponseWrapper<>("OK", null, "User deleted");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/requests")
	@ResponseBody
	public ResponseWrapper<Iterable<Request>> getAllRequests() {
		return new ResponseWrapper<Iterable<Request>>("OK", requestRepository.findAll(), "");
	}

	@PutMapping(path = "/requests/{requestId}")
	@ResponseBody
	public ResponseWrapper<Request> postRequest(@RequestParam Boolean approved,
			@PathVariable(name = "requestId") Integer requestId) {
		try {
			Optional<Request> request = requestRepository.findById(requestId);
			if (request.isEmpty())
				throw new Exception("Could not find that request!");

			Request req = request.get();
			req.setApproved(approved);
			req = requestRepository.save(req);
			return new ResponseWrapper<Request>("OK", req, "Request updated!");
		} catch (Exception e) {
			return new ResponseWrapper<Request>("ERROR", null, e.getMessage());
		}
	}
}
