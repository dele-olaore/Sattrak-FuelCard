package com.dexter.fmr.restresponse;

import java.io.Serializable;

public class RestResponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String message;
	private boolean success;
	
	private String user_id;
	private String fullname;
	
	public RestResponse()
	{}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
}
