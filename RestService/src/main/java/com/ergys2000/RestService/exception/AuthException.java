package com.ergys2000.RestService.exception;

/** A custom exception used to catch errors on the authentication of a user */
public class AuthException extends Exception {
	public AuthException(String message) {
		super(message);
	}
} 
