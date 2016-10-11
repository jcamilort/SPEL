package com.example.eventlifecycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.mundo.Evento;
import com.example.proyectogrado.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class EventSaveForLater {
	private int numberSavedEvents;
	private Evento actual;
	private ArrayList<Evento> savedEvents;
	public final static String number_saved_events = "number_saved_events";
	public final static String event_title = "event_title";
	public final static String event_description = "event_description";
	public final static String event_starttime = "event_starttime";
	public final static String event_endtime = "event_endtime";
	public final static String event_location = "event_location";
	private static final String FILE_URL = "saved_for_later_events";
	private Context context;
	
	public EventSaveForLater(Context cont)
	{
		context = cont;
		load();
	}
	
	public void load() {
		SharedPreferences sharedPref =  context.getSharedPreferences(FILE_URL, Context.MODE_PRIVATE);
		savedEvents = new ArrayList<Evento>();
		numberSavedEvents = sharedPref.getInt(number_saved_events, 0);
		for( int  i = 1; i <= numberSavedEvents ; i++)
		{
			Evento event= new Evento();
			event.setTitle(sharedPref.getString(event_title+i, ""));
			event.setDescription(sharedPref.getString(event_description+i, ""));
			

			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
			try {
				event.setStartTime(sdf.parse(sharedPref.getString(event_starttime+i, "")));
				event.setEndTime(sdf.parse(sharedPref.getString(event_endtime+i, "")));
			} catch (ParseException e) {
				event.setStartTime(new Date());
				event.setEndTime(new Date());
			}
			
			event.setLocation(sharedPref.getString(event_location+i, ""));
			if(!event.getTitle().equals(""))
			{
				savedEvents.add(event);
			}
		}
		
	}
	
	protected void persist() {

		SharedPreferences sharedPref = context.getSharedPreferences(FILE_URL, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		for(int i = 0 ; i<=savedEvents.size() ; i++)
		{
			Evento ev = savedEvents.get(i);
			editor.putString(event_title+i,ev.getTitle() );
			editor.putString(event_description+i,ev.getDescription() );

			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
			
			editor.putString(event_starttime+i,sdf.format(ev.getStartTime() ));
			editor.putString(event_endtime+i,sdf.format(ev.getEndTime()) );
			
			editor.putString(event_location+i,ev.getLocation() );			
		}
		editor.commit();
	}
	
	public void saveEventForLater(Evento event)
	{
		savedEvents.add(event);
		numberSavedEvents++;		
	}
	
	public ArrayList<Evento> getEvents()
	{
		return savedEvents;
	}
	
	public void reset()
	{
		savedEvents.clear();
		numberSavedEvents=0 ;
		//Reset saved preferences
		context.getSharedPreferences(FILE_URL, Context.MODE_PRIVATE).edit().clear().commit();
	}

	public boolean removeEvent(Evento event)
	{
		return savedEvents.remove(event);
	}
}
