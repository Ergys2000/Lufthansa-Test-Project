package com.ergys2000.RestService.controllers;

import java.util.Optional;
import java.util.Random;

import com.ergys2000.RestService.models.ResetPasswordRequest;
import com.ergys2000.RestService.models.ResetToken;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.ResetTokenRepository;
import com.ergys2000.RestService.repositories.UserRepository;
import com.ergys2000.RestService.services.EmailService;
import com.ergys2000.RestService.util.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	private ResetTokenRepository resetTokenRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@GetMapping(path = "/{email}")
	@ResponseBody
	public ResponseWrapper<ResetToken> askForRestToken(@PathVariable(name = "email") String email) {
		try {
			Optional<User> user = userRepository.findByEmail(email);
			if (user.isEmpty())
				throw new Exception("Sorry could not find user with that email!");

			/* Delete the previously created tokens for this user */
			resetTokenRepository.deleteByUserId(user.get().getId());

			/* Create and insert the new token */
			Integer token = generateToken();
			ResetToken resToken = new ResetToken();
			resToken.setToken(token);
			resToken.setUser(user.get());
			resetTokenRepository.save(resToken);

			emailService.sendSimpleMessage(email, "Reset password", "Your reset code is: " + token);

			return new ResponseWrapper<>("OK", resToken, "The token was successfully generated!");
		} catch (Exception e) {
			return new ResponseWrapper<>("ERROR", null, e.getMessage());
		}
	}

	@GetMapping(path = "/{email}/{token}")
	@ResponseBody
	public ResponseWrapper<Boolean> verifyTokenForEmail(@PathVariable(name = "email") String email,
			@PathVariable(name = "token") Integer token) {
		try {
			Optional<ResetToken> resToken = resetTokenRepository.findByEmail(email);
			if (resToken.isEmpty())
				throw new Exception("Sorry no user found with that email!");
			if (!resToken.get().getToken().equals(token))
				throw new Exception("Sorry, wrong token!");

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
			Optional<ResetToken> resToken = resetTokenRepository.findByEmail(email);
			if (resToken.isEmpty())
				throw new Exception("Sorry, your token does not exist!");
			if (!resToken.get().getToken().equals(token))
				throw new Exception("Sorry, your token is wrong!");

			Optional<User> user = userRepository.findByEmail(email);
			if (user.isEmpty())
				throw new Exception("Sorry, no user found with that email!");

			user.get().setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
			userRepository.save(user.get());

			/* Remove the token now that it has been used */
			resetTokenRepository.delete(resToken.get());

			return new ResponseWrapper<Boolean>("OK", null, "Password changed successfully");
		} catch (Exception e) {
			return new ResponseWrapper<Boolean>("ERROR", null, e.getMessage());
		}
	}

	private Integer generateToken() {
		return (Integer) new Random().nextInt(100000);
	}

}
