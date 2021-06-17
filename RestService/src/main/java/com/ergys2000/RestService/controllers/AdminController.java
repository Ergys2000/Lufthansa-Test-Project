package com.ergys2000.RestService.controllers;

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
@RequestMapping(path = "/admin/{adminId}")
public class AdminController {

	@Autowired
	private UserService userService;

	@GetMapping(path = "")
	@ResponseBody
	public ResponseWrapper<User> getAdmin(@PathVariable(name = "adminId") Integer adminId) {
		try {
			return new ResponseWrapper<User>("OK", userService.findUserById(adminId), "Users retrieved successfully!");
		} catch (Exception e) {
			return new ResponseWrapper<User>("ERROR", null, e.getMessage());
		}
	}

	@PutMapping(path = "")
	@ResponseBody
	public ResponseWrapper<User> postUser(@PathVariable(name = "adminId") Integer adminId, @RequestBody User user) {
		try {
			return new ResponseWrapper<>("OK", userService.updateUser(user), "User saved!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", user, e.getMessage());
		}
	}

	@GetMapping(path = "/users")
	@ResponseBody
	public ResponseWrapper<Iterable<User>> getAllUsers() {
		return new ResponseWrapper<Iterable<User>>("OK", userService.findAllUsers(), "Users retrieved successfully!");
	}

	@PostMapping(path = "/users")
	@ResponseBody
	public ResponseWrapper<User> postUser(@RequestBody User user) {
		try {
			return new ResponseWrapper<>("OK", userService.insertUser(user), "User added!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@PutMapping(path = "/users/{userId}")
	@ResponseBody
	public ResponseWrapper<Object> putUser(@PathVariable(name = "userId") Integer userId, @RequestBody User newUser) {
		try {
			return new ResponseWrapper<>("OK", userService.updateUser(newUser), "User updated!");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper<>("ERROR", newUser, e.getMessage());
		}
	}

	@DeleteMapping(path = "/users/{userId}")
	@ResponseBody
	public ResponseWrapper<Object> deleteUser(@PathVariable(name = "userId") Integer userId) {
		try {
			userService.deleteUserById(userId);
			return new ResponseWrapper<>("OK", null, "User deleted");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/requests")
	@ResponseBody
	public ResponseWrapper<Iterable<Request>> getAllRequests() {
		return new ResponseWrapper<Iterable<Request>>("OK", userService.findAllRequests(), "");
	}
}
