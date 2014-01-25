package com.dexter.fmr.rest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.dexter.fmr.dao.CarDAO;
import com.dexter.fmr.dao.DriverRecordDAO;
import com.dexter.fmr.dao.UserDAO;
import com.dexter.fmr.model.Car;
import com.dexter.fmr.model.DriverRecord;
import com.dexter.fmr.model.User;
import com.dexter.fmr.restresponse.RestResponse;

@Path("/driverRecord")
public class DriverRecordResource
{
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse process(@FormParam("tranTime") String tranTime, @FormParam("initFuelLvl") BigDecimal initFuelLvl,
			@FormParam("quantity") BigDecimal quantity, @FormParam("finalFuelLvl") BigDecimal finalFuelLvl, 
			@FormParam("odometer") BigDecimal odometer, @FormParam("fuelRate") BigDecimal fuelRate,
			@FormParam("cost") BigDecimal cost, @FormParam("user_id") String user_id, @FormParam("location") String location,
			@FormParam("refNum") String refNum)
	{
		RestResponse response = new RestResponse();
		
		SimpleDateFormat simDtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		DriverRecord dr = new DriverRecord();
		dr.setRefNumber(refNum);
		dr.setCost(cost);
		dr.setFinalFuelLvl(finalFuelLvl);
		dr.setFuelRate(fuelRate);
		dr.setInitFuelLvl(initFuelLvl);
		dr.setOdometer(odometer);
		dr.setQuantity(quantity);
		try {
			dr.setTranTime(simDtFormat.parse(tranTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dr.setLocation(location);
		
		User driver = new UserDAO().getUserById(Long.parseLong(user_id));
		dr.setDriver(driver);
		
		Car c = new CarDAO().getCarByDriver(driver);
		dr.setCar(c);
		dr.setRegNumber(c.getRegNumber());
		
		boolean ret = new DriverRecordDAO().createDriverRecord(dr);
		response.setSuccess(ret);
		if(ret)
		{
			response.setMessage("Record saved successfully!");
		}
		else
		{
			response.setMessage("Error occured during save process!");
		}
		
		return response;
	}
}
