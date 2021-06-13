package com.ergys2000.RestService.util;

/** This is a response wrapper class. It enforces the structure that each
* response should have when being returned by each api endpoint */
public class ResponseWrapper<T> {
	private String status;
	private T result;
	private String message;

    public ResponseWrapper(String status, T result, String message) {
        this.status = status;
        this.result = result;
        this.message = message;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = result;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
