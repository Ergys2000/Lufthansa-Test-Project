package com.ergys2000.RestService.filters;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ergys2000.RestService.exception.AuthException;
import com.ergys2000.RestService.exception.SkipAuthException;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.repositories.UserRepository;
import com.ergys2000.RestService.services.MyUserDetailsService;
import com.ergys2000.RestService.util.JwtUtil;
import com.ergys2000.RestService.util.ResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	private static final Logger logger = LogManager.getLogger(JwtRequestFilter.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MyUserDetailsService myUserDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		logger.debug("The jwt filter is running...");
		final String authorizationHeader = request.getHeader("Authorization");
		try {

			/*
			 * The request path, for example: /users/1, /supervisor/2/requests
			 */
			String path = request.getServletPath();
			String[] parts = path.split("/");
			if (parts.length == 2 && parts[1].equals("authenticate")) {
				throw new SkipAuthException("We are authenticating, so we skip the next checks.");
			}
			/*
			 * Get the user type and user id from the url
			 */
			String userType = parts[1];
			Integer userId = Integer.parseInt(parts[2]);

			/* Get the user this requests pretends to be */
			Optional<User> pretendedUser = userRepository.findById(userId);

			if (pretendedUser.isEmpty())
				throw new AuthException("User not found!");
			if (!pretendedUser.get().getType().equals(userType))
				throw new AuthException("Your user type does not match the endpoint.");
			if (authorizationHeader == null)
				throw new AuthException("Authorization header not included.");
			if (!authorizationHeader.startsWith("Bearer "))
				throw new AuthException("Authorization type not supported.");

			String username = null;
			String jwt = null;

			jwt = authorizationHeader.substring(7);
			username = jwtUtil.extractUsername(jwt);

			if (username == null)
				throw new AuthException("No username was included with your token!");

			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails pretendedUserDetails = myUserDetailsService
						.loadUserByUsername(pretendedUser.get().getEmail());
				/*
				 * Check if the given jwt corresponds to the user the request prenteds to be
				 */
				if (jwtUtil.validateToken(jwt, pretendedUserDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							pretendedUserDetails, null, pretendedUserDetails.getAuthorities());

					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

				}
			}
			filterChain.doFilter(request, response);
		} catch (AuthException | ExpiredJwtException e) {
			ObjectMapper mapper = new ObjectMapper();
			ResponseWrapper<String> resWrapper = new ResponseWrapper<String>("ERROR", null, e.getMessage());
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getOutputStream().print(mapper.writeValueAsString(resWrapper));
			logger.debug("Authentication with jwt token failed");
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		} catch (SkipAuthException e) {
			logger.debug("The authentication is skipping...");
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			logger.debug("The authentication is skipping...");
			filterChain.doFilter(request, response);
		}
	}
}
