package com.example.eventlifecycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class UserValidation{
	public static final String FILE_URL = "user_validation_details";
	public static final double threshold = 0.5;
	public static final int TIME_TO_FORGIVE_DAYS = 20;
	
	public final static String user_calification = "USERCALIFICATION";
	public final static String user_reports_quantity = "USERREPORTSQUANTITY";
	public final static String user_disabled = "USERDISABLED";
	public final static String user_last_forgiven = "USERLASTFORGIVEN";
	private double calification = 0;
	private Date lastForgiven;
	public int reportsQuanitity = 0;
	public boolean disabled = false;
	private Context context;
	private static UserValidation uInstance = null;
	
	public UserValidation(Context cont)
	{
		context = cont;
		loadData();
	}
	
	public static UserValidation getInstance(Context cont){
		if(uInstance==null)
		{
			uInstance = new UserValidation(cont);
		}
		return uInstance;
		
	}
	

	public double getCalification()
	{
		return calification;
	}
	public Date getLastForgiven()
	{
		return lastForgiven;
	}
	public void loadData()
	{
		SharedPreferences sharedPref =  context.getSharedPreferences(FILE_URL, Context.MODE_PRIVATE);
		calification = sharedPref.getFloat(user_calification, 0);
		reportsQuanitity = sharedPref.getInt(user_reports_quantity, 0);
		disabled =  sharedPref.getBoolean(user_disabled, false);
		String lastForgivenDate = sharedPref.getString(user_last_forgiven, "NONE");
		if(!lastForgivenDate.equals("NONE"))
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
			try {
				lastForgiven = simpleDateFormat.parse(lastForgivenDate);
			} catch (ParseException e) {
				lastForgiven = null;
			}
		}
	}

	public void persist()
	{
		SharedPreferences sharedPref = context.getSharedPreferences(FILE_URL, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putFloat(user_calification, (float) calification);
		editor.putInt(user_reports_quantity, reportsQuanitity);
		editor.putBoolean(user_disabled, disabled);
		if(!lastForgiven.equals("NONE"))
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
			String stringLastForgiven = simpleDateFormat.format(lastForgiven);
			editor.putString(user_last_forgiven, stringLastForgiven);
			
		}
		editor.commit();

	}
	public void update(double newEventCalification)
	{
		if(disabled)
		{
			if(lastForgiven.getTime() - System.currentTimeMillis()  > TIME_TO_FORGIVE_DAYS*24*60*1000)
			{
				lastForgiven = new Date(System.currentTimeMillis());
				disabled = false;
			}
		}
		else{
			calification=((calification*reportsQuanitity)+newEventCalification)/(reportsQuanitity+1);
			reportsQuanitity++;
			if(calification<threshold)
			{
				disabled=true;
				lastForgiven = new Date(System.currentTimeMillis());
			}
		}
		persist();
	}
	public boolean userDisabled()
	{
		return disabled;
	}
}
