package com.example.eventlifecycle;

import java.text.SimpleDateFormat;

import com.example.mundo.Evento;
import com.example.proyectogrado.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class EventFlusherActivity extends Activity{
	public EventSaveForLater eSaveLater;
	private Evento actual;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		eSaveLater = new EventSaveForLater(getApplicationContext());

		if(eSaveLater.getEvents().size()>0){
			actual = eSaveLater.getEvents().get(0); 
			setContentView(R.layout.event_for_later_layout);
			TextView title= (TextView)findViewById(R.id.forLaterTitle);
			TextView desc= (TextView)findViewById(R.id.forLaterDesc);
			TextView startTime= (TextView)findViewById(R.id.forLaterST);
			TextView endTime= (TextView)findViewById(R.id.forLaterET);
			TextView location= (TextView)findViewById(R.id.forLaterLoc);
			
			title.setText(actual.getTitle());
			desc.setText(actual.getDescription());
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
			startTime.setText(sdf.format(actual.getStartTime()));
			endTime.setText(sdf.format(actual.getEndTime()));
			
			location.setText(actual.getLocation());
		}
		else {
			setContentView(R.layout.events_for_later_empty);
			actual = null;
		}
	}

	public void onSend(View view){
		if(EventRegistration.getInstance().sendToServer(actual)){
			eSaveLater.removeEvent(actual);
			if(eSaveLater.getEvents().size()>0)
				actual = eSaveLater.getEvents().get(0); 
			else 
				actual = null;
		}
		else{
			Toast.makeText(getApplicationContext(), "No se ha podido enviar, trata de nuevo", Toast.LENGTH_SHORT).show();
		}
		
		if(eSaveLater.getEvents().size()>0)
			actual = eSaveLater.getEvents().get(0); 
		else 
			actual = null;
		this.recreate();
	}
	public void onDelete(View view){
		eSaveLater.getEvents().remove(actual);
		if(eSaveLater.getEvents().size()>0)
			actual = eSaveLater.getEvents().get(0); 
		else 
			actual = null;
		this.recreate();
	}

}
