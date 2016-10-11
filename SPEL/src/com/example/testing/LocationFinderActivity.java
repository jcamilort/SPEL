package com.example.testing;

import com.example.broadcasting.BroadcastConstants;
import com.example.geoloc.LocationFinder;
import com.example.proyectogrado.CrearEventoActivity;
import com.example.proyectogrado.PushValidationActivity;
import com.example.proyectogrado.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LocationFinderActivity extends Activity{

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//TODO get broadcast and assign them to view
			Toast.makeText(getApplicationContext(), "received", Toast.LENGTH_SHORT).show();
			String action = intent.getAction();
			if(action.equals(LocationFinder.LF_FOUND_LOC_GPS)){
				String latitud = intent.getExtras().getString("latitud");
				String longitud = intent.getExtras().getString("longitud");
				String lugar = intent.getExtras().getString("lugar");
				if(lugar==null)
				{
					lugar = "Fuera del campus";
				}

				TextView txtGPS = (TextView)findViewById(R.id.txtGPS);
				if(longitud==null||latitud==null)
					txtGPS.setText("No Disponible");
				else
					txtGPS.setText("Lugar: "+lugar+"\n"+longitud+" longitud "+latitud+" latitud");
			}
			else if(action.equals(LocationFinder.LF_FOUND_LOC_BEACON)){
				String major = intent.getExtras().getString("majorbeacon");
				String minor = intent.getExtras().getString("minorbeacon");

				TextView txtGPS = (TextView)findViewById(R.id.txtBeacon);
				if(major==null||minor==null)
					txtGPS.setText("No Disponible");
				else
					txtGPS.setText(major+" Major "+minor+" Minor");
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.proy_grad, menu);
		return true;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_finder_check);

		IntentFilter filter = new IntentFilter();
		filter.addAction(LocationFinder.LF_FOUND_LOC_BEACON);
		filter.addAction(LocationFinder.LF_FOUND_LOC_GPS);
		registerReceiver(receiver, filter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_nuevo_evento:
			Intent intent = new Intent(this, CrearEventoActivity.class);
			startActivity(intent);
			Toast.makeText(this, "Quiero reportar un nuevo evento", Toast.LENGTH_SHORT).show();
			break;
		case R.id.location_testing:
			Intent intent2 = new Intent(this, LocationFinderActivity.class);
			startActivity(intent2);
			Toast.makeText(this, "Location Test", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}

		return true;
	} 
	/**
	@Override
	public void onDestroy()
	{
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	*/
	public void validateEvent(View view)
	{
    	Log.d("Broadcast DEBUG", "Button Pressed");
		
		Intent i = new Intent();
    	i.setAction(BroadcastConstants.EVENT_FOR_VALIDATION);
    	i.putExtra("title", "Evento de Prueba");
    	i.putExtra("location", "ML-567");
    	i.putExtra("starttime", "11/11/2014 4:00 PM");
		sendBroadcast(i);
    	Log.d("Broadcast DEBUG", "Validation Sent");
	}

}
