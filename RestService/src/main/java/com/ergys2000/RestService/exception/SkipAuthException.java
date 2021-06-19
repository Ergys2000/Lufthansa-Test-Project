package com.ergys2000.RestService.exception;

/** A custom exception which indicates that the authentication should be
* skipped for this user */
public class SkipAuthException extends Exception {
	public SkipAuthException(String message) {
		super(message);
	}
} 
