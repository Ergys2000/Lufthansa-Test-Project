package com.ergys2000.RestService.controllers;

import java.util.ArrayList;

import javax.security.auth.message.AuthException;

import com.ergys2000.RestService.models.AuthenticationRequest;
import com.ergys2000.RestService.models.AuthenticationResponse;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.UserRepository;
import com.ergys2000.RestService.services.MyUserDetailsService;
import com.ergys2000.RestService.util.JwtUtil;
import com.ergys2000.RestService.util.ResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping(path = "/authenticate")
public class AuthenticationController {
	private static final Logger logger = LogManager.getLogger(AuthenticationController.class);
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping
	@ResponseBody
	public ResponseWrapper<AuthenticationResponse> createAuthToken(@RequestBody AuthenticationRequest authRequest) {
		logger.debug("{} is trying to authenticate", authRequest.getUsername());
		try {
			final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

			if (!passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword()))
				throw new Exception("Wrong credentials!");

			final String jwt = jwtUtil.generateToken(userDetails);
			final User user = userDetailsService.getUserByUsername(authRequest.getUsername());

			return new ResponseWrapper<>("OK", new AuthenticationResponse(user.getId(), jwt, user.getType()),
					"Token generated!");
		} catch (Exception e) {
			logger.debug("Authentication failed for {}", authRequest.getUsername());
			return new ResponseWrapper<>("ERROR", null, "Wrong credentials!");
		}
	}
}
