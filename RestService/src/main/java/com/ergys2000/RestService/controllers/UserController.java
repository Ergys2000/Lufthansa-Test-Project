package com.ergys2000.RestService.controllers;

import com.ergys2000.RestService.models.ChangePasswordRequest;
import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.services.UserService;
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
	private UserService userService;

	@GetMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<User> getUser(@PathVariable(name = "id") Integer userId) {
		try {
			return new ResponseWrapper<User>("OK", userService.findUserById(userId), "User retrieved successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@PutMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<User> putUser(@PathVariable(name = "id") Integer userId, @RequestBody User user) {
		try {
			User u = userService.updateUser(user);
			return new ResponseWrapper<>("OK", u, "User saved!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", user, e.getMessage());
		}
	}

	@PutMapping(path = "/{id}/changepassword")
	@ResponseBody
	public ResponseWrapper<User> changePassword(@PathVariable(name = "id") Integer userId,
			@RequestBody ChangePasswordRequest changePasswordReq) {
		try {
			userService.changePassword(userId, changePasswordReq);
			return new ResponseWrapper<>("OK", null, "User password updated!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/{id}/requests")
	@ResponseBody
	public ResponseWrapper<Iterable<Request>> getAllRequests(@PathVariable(name = "id") Integer userId) {
		return new ResponseWrapper<Iterable<Request>>("OK", userService.findRequestsByUserId(userId), "");
	}

	@PostMapping(path = "/{id}/requests")
	@ResponseBody
	public ResponseWrapper<Request> postRequest(@RequestBody Request request,
			@PathVariable(name = "id") Integer userId) {
		try {
			Request r = userService.insertRequest(request, userId);
			return new ResponseWrapper<Request>("OK", r, "Request added!");
		} catch (Exception e) {
			return new ResponseWrapper<Request>("ERROR", request, e.getMessage());
		}
	}

	@DeleteMapping(path = "/{id}/requests/{requestId}")
	@ResponseBody
	public ResponseWrapper<Object> deleteRequest(@PathVariable(name = "requestId") Integer requestId,
			@PathVariable(name = "id") Integer userId) {
		try {
			userService.deleteRequest(requestId);
			return new ResponseWrapper<Object>("OK", null, "Request deleted!");
		} catch (Exception e) {
			return new ResponseWrapper<Object>("ERROR", null, e.getMessage());
		}
	}

}
