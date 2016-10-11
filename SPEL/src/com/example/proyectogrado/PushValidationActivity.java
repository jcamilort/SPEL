package com.example.proyectogrado;

import com.example.broadcasting.BroadcastConstants;
import com.example.geoloc.LocationFinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class PushValidationActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();
    	Log.d("Broadcast DEBUG", "Intent received and Act creadted");
		if(intent.getAction().equals(BroadcastConstants.EVENT_FOR_VALIDATION_DIALOG)){
			String title = intent.getStringExtra("title");
			String location = intent.getStringExtra("location");
			String starttime = intent.getStringExtra("starttime");


			AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  
			dialogo1.setTitle("Hay un nuevo evento cerca de ti!");  
			dialogo1.setMessage("Alguien ha reportado un evento cerca a tu ubicacion, podrias confirmarlo?"+
					"\nEvento: "+title+
					"\nLugar: "+location+
					"\nHora de inicio: "+starttime
					); 
			dialogo1.setPositiveButton(R.string.validacion_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					Intent sendIntent = new Intent();
					sendIntent.setAction(BroadcastConstants.EVENT_VALIDATED);
					sendIntent.putExtra("VALIDACION_EVENTO", "CONFIRMADO");
					sendBroadcast(sendIntent);

			        finish();
				}
			});
			dialogo1.setNegativeButton(R.string.validacion_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent sendIntent = new Intent();
					sendIntent.setAction(BroadcastConstants.EVENT_VALIDATED);
					sendIntent.putExtra("VALIDACION_EVENTO", "NEGADO");
					sendBroadcast(sendIntent);

			        finish();
				}
			});
			dialogo1.setNeutralButton(R.string.validacion_neutral, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Intent sendIntent = new Intent();
					sendIntent.setAction(BroadcastConstants.EVENT_VALIDATED);
					sendIntent.putExtra("VALIDACION_EVENTO", "IGNORADO");
					sendBroadcast(sendIntent);

			        finish();
				}
			});
			dialogo1.create();
			dialogo1.show();
		}

	}
}