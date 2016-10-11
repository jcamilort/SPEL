package com.example.broadcasting;

import com.example.eventlifecycle.EventFlusherActivity;
import com.example.eventlifecycle.EventSaveForLater;
import com.example.proyectogrado.CrearEventoActivity;
import com.example.proyectogrado.PushValidationActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AnnouncerLocalEventos extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//TODO definir actions y filtros
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
        	Log.d("BROADCAST RECEIVER", "Connectivity Change");
        	Intent i = new Intent(context,EventFlusherActivity.class );
        	i.setAction(BroadcastConstants.EVENT_SAVE_FOR_LATER_DISPATCH);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
        } else if (intent.getAction().equals(BroadcastConstants.EVENT_FOR_REGISTER)) {
        	intent.putExtra("MY_KEY", "MY_VALUE"); //Esto va en a actividad que manda el broadcast
        	intent.getExtras().getString("MY_KEY");
        	
        	//Saca la info del evento
        	//Crea el obejto evento
        	//Si hay conexion, envia el evento
        	//Si no hay conexion, guarda el evento para enviarlo despues
        	
        }else if (intent.getAction().equals(BroadcastConstants.EVENT_VALIDATED)) {
        	String respuesta = intent.getExtras().getString("VALIDACION_EVENTO");
			Toast.makeText(context, "Has "+respuesta+" el evento.", Toast.LENGTH_SHORT).show();
			//TODO EventValidation to server
        	
        }else if (intent.getAction().equals(BroadcastConstants.EVENT_REGISTERED)) {
        	
        }else if (intent.getAction().equals(BroadcastConstants.INCOMING_EVENT_FOR_VALIDATION)) {
        	//TOGO Recoge un evento que tiene que ser validado (YES/NO option).
        	
        }
        else if (intent.getAction().equals(BroadcastConstants.EVENT_FOR_VALIDATION)) {
        	
        	Log.d("Broadcast DEBUG", "Validation Request received");
        	Intent i = new Intent(context,PushValidationActivity.class );
        	i.setAction(BroadcastConstants.EVENT_FOR_VALIDATION_DIALOG);
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	i.putExtra("title", intent.getStringExtra("title"));
        	i.putExtra("location", intent.getStringExtra("location"));
        	i.putExtra("starttime", intent.getStringExtra("starttime"));
			context.startActivity(i);
        }
   }

	/**
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		   @Override
		   public void onReceive(Context context, Intent intent) {
		    //TODO definir actions y filtros
				if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
		            //TODO validar si vuelve a enviar el ultimo vento guardado
		        } else if (intent.getAction().equals(BroadcastConstants.EVENT_FOR_REGISTER)) {
		        	intent.putExtra("MY_KEY", "MY_VALUE"); //Esto va en a actividad que manda el broadcast
		        	intent.getExtras().getString("MY_KEY");
		        	
		        	//Saca la info del evento
		        	//Crea el obejto evento
		        	//Si hay conexion, envia el evento
		        	//Si no hay conexion, guarda el evento para enviarlo despues
		        	
		        }else if (intent.getAction().equals(BroadcastConstants.EVENT_VALIDATED)) {
		        	String respuesta = intent.getExtras().getString("VALIDACION_EVENTO");
					Toast.makeText(context, "Respuesta: "+respuesta, Toast.LENGTH_SHORT).show();
		        	
		        }else if (intent.getAction().equals(BroadcastConstants.EVENT_REGISTERED)) {
		        	
		        }else if (intent.getAction().equals(BroadcastConstants.INCOMING_EVENT_FOR_VALIDATION)) {
		        	//TOGO Recoge un evento que tiene que ser validado (YES/NO option).
		        	
		        }
		        else if (intent.getAction().equals(BroadcastConstants.EVENT_FOR_VALIDATION)) {
		        	
		        	Log.d("Broadcast DEBUG", "Validation Request received");
		        	Intent i = new Intent(context,PushValidationActivity.class );
		        	i.setAction(BroadcastConstants.EVENT_FOR_VALIDATION_DIALOG);
		        	i.putExtra("title", intent.getStringExtra("title"));
		        	i.putExtra("location", intent.getStringExtra("location"));
		        	i.putExtra("starttime", intent.getStringExtra("starttime"));
					context.startActivity(i);
		        }
		   }
		};
		@Override
		public void onCreate (Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			//TODO
			filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
			filter.addAction(BroadcastConstants.EVENT_FOR_REGISTER); 
			filter.addAction(BroadcastConstants.EVENT_FOR_VALIDATION);
			filter.addAction(BroadcastConstants.EVENT_FOR_VALIDATION_DIALOG); 
			filter.addAction(BroadcastConstants.EVENT_REGISTERED);
			filter.addAction(BroadcastConstants.INCOMING_EVENT_FOR_VALIDATION); 

			registerReceiver(receiver, filter);
		}

		@Override
		public void onDestroy ()
		{
			unregisterReceiver(receiver);
		}

	// Our handler for received Intents. This will be called whenever an Intent
	// with an action named "custom-event-name" is broadcasted.
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    // Get extra data included in the Intent
	    String message = intent.getStringExtra("message");
	    Log.d("receiver", "Got message: " + message);
	  }
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
	}*/
}
