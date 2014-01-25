package com.dexter.fmr.util;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Synchronized;

import java.util.List;
import java.util.Random;

@Name("appOptions")
@Scope(ScopeType.APPLICATION)
@Startup
@Synchronized
public class AppOptions implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String defaultSkin;
	private List<String> availableSkins;
	
	private int ranNum;
	
	Random rnd = new Random(10000000);
	
	public AppOptions()
	{}
	
	public String getDefaultSkin()
	{
		return defaultSkin;
	}
	
	public void setDefaultSkin(String defaultSkin)
	{
		this.defaultSkin = defaultSkin;
	}
	
	public List<String> getAvailableSkins()
	{
		return availableSkins;
	}
	
	public void setAvailableSkins(List<String> availableSkins)
	{
		this.availableSkins = availableSkins;
	}

	public int getRanNum() {
		reRan();
		return ranNum;
	}
	
	public void reRan()
	{
		ranNum = rnd.nextInt();
	}
}
