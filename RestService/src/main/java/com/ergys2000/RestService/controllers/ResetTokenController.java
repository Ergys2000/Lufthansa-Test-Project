package com.ergys2000.RestService.controllers;

import com.ergys2000.RestService.models.ResetPasswordRequest;
import com.ergys2000.RestService.models.ResetToken;
import com.ergys2000.RestService.services.UserService;
import com.ergys2000.RestService.util.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/resetpassword")
public class ResetTokenController {
	@Autowired
	private UserService userService;

	@GetMapping(path = "/{email}")
	@ResponseBody
	public ResponseWrapper<ResetToken> askForRestToken(@PathVariable(name = "email") String email) {
		try {
			userService.createTokenForUser(email);
			return new ResponseWrapper<>("OK", null, "The token was successfully generated!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/{email}/{token}")
	@ResponseBody
	public ResponseWrapper<Boolean> verifyTokenForEmail(@PathVariable(name = "email") String email,
			@PathVariable(name = "token") Integer token) {
		try {
			userService.verifyToken(email, token);
			return new ResponseWrapper<Boolean>("OK", true, "Token verified successfully");
		} catch (Exception e) {
			return new ResponseWrapper<Boolean>("ERROR", false, e.getMessage());
		}
	}

	@PutMapping(path = "/{email}/{token}")
	@ResponseBody
	public ResponseWrapper<Boolean> changePasswordWithToken(@PathVariable(name = "email") String email,
			@PathVariable(name = "token") Integer token, @RequestBody ResetPasswordRequest resetPasswordRequest) {
		try {
			userService.resetPassword(email, token, resetPasswordRequest);
			return new ResponseWrapper<Boolean>("OK", null, "Password changed successfully");
		} catch (Exception e) {
			return new ResponseWrapper<Boolean>("ERROR", null, e.getMessage());
		}
	}

}
