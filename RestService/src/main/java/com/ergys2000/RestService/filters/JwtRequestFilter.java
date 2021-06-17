package com.ergys2000.RestService.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ergys2000.RestService.exception.AuthException;
import com.ergys2000.RestService.exception.SkipAuthException;
import com.ergys2000.RestService.models.User;
import com.ergys2000.RestService.services.MyUserDetailsService;
import com.ergys2000.RestService.services.UserService;
import com.ergys2000.RestService.util.JwtUtil;
import com.ergys2000.RestService.util.ResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	private static final Logger logger = LogManager.getLogger(JwtRequestFilter.class);

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

			String username = null;
			String jwt = null;

			jwt = authorizationHeader.substring(7);
			username = jwtUtil.extractUsername(jwt);

			if (username == null)
				throw new AuthException("No username was included with your token!");

			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails pretendedUserDetails = myUserDetailsService
						.loadUserByUsername(username);
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
			e.printStackTrace();
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		} catch (Exception e) {
			logger.debug("The authentication is skipping...");
			filterChain.doFilter(request, response);
		}
	}
}
