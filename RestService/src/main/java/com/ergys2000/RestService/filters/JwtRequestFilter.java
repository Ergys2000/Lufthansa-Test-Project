package com.ergys2000.RestService.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ergys2000.RestService.exception.AuthException;
import com.ergys2000.RestService.services.MyUserDetailsService;
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
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

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

			if (SecurityContextHolder.getContext().getAuthentication() == null && username != null) {
				UserDetails pretendedUserDetails = myUserDetailsService.loadUserByUsername(username);
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
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		} finally {
			filterChain.doFilter(request, response);
		}
	}
}
