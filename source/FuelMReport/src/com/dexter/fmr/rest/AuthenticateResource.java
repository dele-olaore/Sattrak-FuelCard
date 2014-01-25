package com.dexter.fmr.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.dexter.fmr.dao.UserDAO;
import com.dexter.fmr.model.User;
import com.dexter.fmr.restresponse.RestResponse;

@Path("/authenticate")
public class AuthenticateResource
{
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse process(@FormParam("username") String username, @FormParam("password") String password)
	{
		RestResponse response = new RestResponse();
		
		User u = new UserDAO().getUserByLogin(username, password);
		if(u != null)
		{
			response.setFullname(u.getFullname());
			response.setSuccess(true);
			response.setUser_id(""+u.getId());
		}
		else
		{
			response.setMessage("Authentication failed");
			response.setSuccess(false);
		}
		
		return response;
	}
}
