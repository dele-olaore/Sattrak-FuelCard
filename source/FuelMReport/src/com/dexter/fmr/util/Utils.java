package com.dexter.fmr.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class Utils
{
	/*
	 * Private members for database access, retrieval and manipulation.
	 * */
	private Connection con;
	private CallableStatement stored_procedure;
	private ResultSet result;
	
	private int output = -1;
	// private String empty = "";
	
	public Utils()
	{}
	
	/**
	 * Gets the current class loader.
	 * */
	protected static ClassLoader getCurrentClassLoader(Object defaultObject)
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if(loader == null)
		{
			loader = defaultObject.getClass().getClassLoader();
		}

		return loader;
	}

	/**
	 * Gets a string mapped to the specified key from the current resource bundle
	 * */
	public static String getMessageResourceString(String bundleName, String key, Object params[], 
		Locale locale)
	{
		String text = null;

		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentClassLoader(params));

		try
		{
			text = bundle.getString(key);
		}
		catch(MissingResourceException e)
		{
			text = "?? key " + key + " not found ??";
		}

		if(params != null)
		{
			MessageFormat mf = new MessageFormat(text, locale);
			text = mf.format(params, new StringBuffer(), null).toString();
		}

		return text;
	}
	
	/**
	 * Gets the string using the above methods.
	 * */
	public static String getBundleMessage(String key, Object[] params)
	{
		FacesContext context = FacesContext.getCurrentInstance();

		String text = getMessageResourceString(context.getApplication()
				.getMessageBundle(), key, params, context.getViewRoot()
				.getLocale());

		return text;
	}
	
	/**
	 * Copies a file from an input to a output.
	 * */
	public static void copyFile(FileInputStream sourceStream, FileOutputStream destinationStream) throws IOException
	{
		FileChannel inChannel = sourceStream.getChannel();
		FileChannel outChannel = destinationStream.getChannel();
		try
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
		catch(IOException e)
		{
			throw e;
		}
		finally
		{
			if(inChannel != null)
			{
				try
				{
					inChannel.close();
				}
				catch(IOException ioe)
				{}
			}
			if(outChannel != null)
				outChannel.close();
		}
	}
	
	/**
	 * Used internally to connect to the database.
	 * */
	private void connectToDB() throws Exception
	{
		closeConnection();
		
		con = Env.getConnection();
	}
	
	/**
	 * Used internally to close the connection after use. 
	 * */
	private void closeConnection()
	{
		if(result != null)
		{
			try
			{
				result.close();
				result = null;
			}
			catch(Exception ignore){}
		}
		if(con != null)
		{
			try
			{
				con.close();
				con = null;
			}
			catch(Exception ignore){}
		}
	}
	
}
