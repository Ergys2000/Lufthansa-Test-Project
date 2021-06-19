package com.ergys2000.RestService.models;

/** Defines the structure of an authentication response */
public class AuthenticationResponse {
	private Integer id;
	private String jwt;
	private String type;

	public AuthenticationResponse(Integer id, String jwt, String type) {
		this.id = id;
		this.jwt = jwt;
		this.type = type;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJwt() {
		return jwt;
	}

	public String getType() {
		return type;
	}

}
