
package com.ergys2000.RestService.controllers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.ergys2000.RestService.models.Request;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.services.UserService;
import com.ergys2000.RestService.util.RequestExcelExporter;
import com.ergys2000.RestService.util.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	private UserService userService;

	@GetMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<User> getUser(@PathVariable(name = "id") Integer userId) {
		try {
			return new ResponseWrapper<>("OK", userService.findUserById(userId), "Users retrieved successfully!");
		} catch (Exception e) {
			return new ResponseWrapper<User>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/{id}/users")
	@ResponseBody
	public ResponseWrapper<Iterable<User>> getSupervisedUsers(@PathVariable(name = "id") Integer userId) {
		try {
			return new ResponseWrapper<>("OK", userService.findUsersBySupervisorId(userId),
					"Users retrieved successfully!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/{id}/users/{requestsUserId}/requests")
	@ResponseBody
	public ResponseWrapper<Iterable<Request>> getRequestsForUser(
			@PathVariable(name = "requestsUserId") Integer userId) {
		try {
			return new ResponseWrapper<>("OK", userService.findRequestsByUserId(userId),
					"Users retrieved successfully!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/{id}/users/{requestsUserId}/requests/export")
	@ResponseBody
	public void exportRequestsToExcep(
			@PathVariable(name = "requestsUserId") Integer userId, HttpServletResponse response) throws IOException {

			response.setContentType("application/octet-stream");
			DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			String currentDateTime = dateFormatter.format(new Date());

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%d_%s.xlsx\"", userId, currentDateTime);
			System.out.println(headerValue);
			response.setHeader(headerKey, headerValue);

			Iterable<Request> requests = userService.findRequestsByUserId(userId);
			RequestExcelExporter exporter = new RequestExcelExporter(requests);
			exporter.export(response);
	}

	@PutMapping(path = "/{id}")
	@ResponseBody
	public ResponseWrapper<User> postUser(@PathVariable(name = "id") Integer userId, @RequestBody User user) {
		try {
			return new ResponseWrapper<>("OK", userService.updateUser(user), "User saved!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", user, "User saved!");
		}
	}

	@GetMapping(path = "/{id}/requests")
	@ResponseBody
	public ResponseWrapper<Iterable<Request>> getAllRequests(@PathVariable(name = "id") Integer userId) {
		return new ResponseWrapper<Iterable<Request>>("OK", userService.findAllRequests(), "");
	}

	@PutMapping(path = "/{id}/requests/{requestId}")
	@ResponseBody
	public ResponseWrapper<Request> postRequest(@RequestParam Boolean approved,
			@PathVariable(name = "id") Integer userId, @PathVariable(name = "requestId") Integer requestId) {
		try {
			return new ResponseWrapper<Request>("OK", userService.resolveRequest(requestId, userId, approved),
					"Request updated!");
		} catch (Exception e) {
			return new ResponseWrapper<Request>("ERROR", null, e.getMessage());
		}
	}
}
