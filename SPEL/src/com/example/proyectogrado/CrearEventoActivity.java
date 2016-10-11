package com.example.proyectogrado;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.broadcasting.BroadcastConstants;
import com.example.eventlifecycle.EventRegistration;
import com.example.eventlifecycle.UserValidation;
import com.example.mundo.Evento;
import com.example.testing.LocationFinderActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CrearEventoActivity extends Activity implements OnSeekBarChangeListener{
	
	private EventRegistration eventRegisterer;
	private UserValidation userValidator;

	private SeekBar duracion;
	private TextView textDuracion;
	private int duracionSeekBar = 0;
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.proy_grad, menu);
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		eventRegisterer=EventRegistration.getInstance();
		userValidator = UserValidation.getInstance(getApplicationContext());

		
		if(userValidator.disabled)
		{

			setContentView(R.layout.event_creation_activity_disabled);
			TextView txtDisabled = (TextView)findViewById(R.id.textDisabled);			

			SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm");
			txtDisabled.setText("La mayoria de tus reportes fueron negados, has quedado inhabilitado hasta"+sdf.format(new Date(userValidator.getLastForgiven().getTime()+UserValidation.TIME_TO_FORGIVE_DAYS*24*60*1000))  );
		}
		else{

			setContentView(R.layout.event_creation_activity);

			//Llena el spinner de tipos de eventos
			Spinner spinner = (Spinner) findViewById(R.id.spinnertipoevento);
			// Create an ArrayAdapter using the string array and a default spinner layout
			
			final EditText otrotext = (EditText)findViewById(R.id.textOtroEvento);
			otrotext.setVisibility(View.INVISIBLE);

			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.tipo_eventos, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

	            @Override
	            public void onItemSelected(AdapterView<?> parent, View view,
	                    int pos, long id) {
	                Log.d("TESTING", parent.getItemAtPosition(pos).toString());
	                if(parent.getItemAtPosition(pos).toString().equals("Otro")) {
	                    otrotext.setVisibility(View.VISIBLE);
	                    otrotext.setHint("Especifica");
	                }
	                else
	                    otrotext.setVisibility(View.INVISIBLE);
	            }

	            @Override
	            public void onNothingSelected(AdapterView<?> arg0) {
	                // TODO Auto-generated method stub

	            }
	        });
			
			duracion = (SeekBar) findViewById(R.id.seekduracion);
			duracion.setOnSeekBarChangeListener(this);
			
			textDuracion = (TextView)findViewById(R.id.textDuracion);
			
			//Llena el campo de ubicacion con la ubicacion del usuario
			EditText donde = (EditText)findViewById(R.id.txtDonde);
			String dondeEstoy = dondeEstoy();
			
			donde.setText(dondeEstoy);
		}
		

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
			/**
			Intent intent2 = new Intent(this, LocationFinderActivity.class);
			startActivity(intent2);
			Toast.makeText(this, "Location Test", Toast.LENGTH_SHORT).show();
			*/

	    	Log.d("Broadcast DEBUG", "Button Pressed");
			
			Intent i = new Intent();
	    	i.setAction(BroadcastConstants.EVENT_FOR_VALIDATION);
	    	i.putExtra("title", "Evento de Prueba");
	    	i.putExtra("location", "ML-567");
	    	i.putExtra("starttime", "11/11/2014 4:00 PM");
			sendBroadcast(i);
	    	Log.d("Broadcast DEBUG", "Validation Sent");
			break;
		default:
			break;
		}

		return true;
	} 
	public void displayMap(View view)
	{/**
		Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.google.com/maps/d/viewer?mid=zfzELMV3_ra0.kEkVaBI-ynsI"));
		intent.setComponent(ComponentName.unflattenFromString("com.google.android.apps.maps/com.google.android.maps.MapsActivity"));
		intent.addCategory("android.intent.category.LAUNCHER");
		// replace string with your Google My Map URL
		//intent.setData(Uri.parse("https://www.google.com/maps/d/viewer?mid=zfzELMV3_ra0.kEkVaBI-ynsI"));
		startActivity(intent);*/
		
		Intent i = new Intent(getApplicationContext(), MapActivity.class);
		startActivity(i);		
	}

	public void registerEvent(View view)
	{
		
		//Toma todos los valore y crea un objeto evento
		Evento evento  = new Evento();
		Spinner tipo = (Spinner)findViewById(R.id.spinnertipoevento);
		Log.d("Spinner EVENT CREATION", tipo.getSelectedItem().toString());
		
		if(tipo.getSelectedItem()==null)
		{
			AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  
			dialogo1.setTitle("Error");  
			dialogo1.setMessage("Selecciona un tipo de evento"); 
			dialogo1.setPositiveButton("OK",null);
			dialogo1.create();
			dialogo1.show();
		}
		else{
			String tipoEvento = tipo.getSelectedItem().toString();
			if(tipoEvento.equals("Otro"))
			{
				EditText edttipo = (EditText)findViewById(R.id.textOtroEvento);
				tipoEvento=edttipo.getText().toString();
			}
			
			EditText txtdonde = (EditText)findViewById(R.id.txtDonde);
			String donde = txtdonde.getText().toString();
			
			EditText txtTitle = (EditText)findViewById(R.id.txtTitle);
			String title = txtTitle.getText().toString();
			
			EditText txtDescripcion = (EditText)findViewById(R.id.txtDescripc);
			String descripcion = txtDescripcion.getText().toString();
			
			if(descripcion==null||descripcion.equals("")||title==null||title.equals("")||donde==null ||
					donde.equals("")||tipoEvento==null||tipoEvento.equals("")){
				AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  
				dialogo1.setTitle("Error");  
				dialogo1.setMessage("Llena todos los campos"); 
				dialogo1.setPositiveButton("OK",null);
				dialogo1.create();
				dialogo1.show();
			}
			else if(duracionSeekBar==0)
			{
				AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  
				dialogo1.setTitle("Error");  
				dialogo1.setMessage("Elige una duracion aproximada"); 
				dialogo1.setPositiveButton("OK",null);
				dialogo1.create();
				dialogo1.show();
			}
			else{
				evento.setDescription(descripcion);
				evento.setTitle(title);
				evento.setLocation(donde);
				
				Calendar c = Calendar.getInstance(); 
				int milliseconds = c.get(Calendar.MILLISECOND);
				
				Date startTime = new Date(milliseconds);
				Date endTime = new Date(milliseconds+duracionSeekBar);
				
				evento.setStartTime(startTime);
				evento.setEndTime(endTime);
				
				ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				 
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null &&
				                      activeNetwork.isConnectedOrConnecting();
				if(isConnected)
				{
					if(eventRegisterer.sendToServer(evento))
					{
						Toast.makeText(this, "Tu Evento ha sido reportado", Toast.LENGTH_SHORT).show();
						Intent i = new Intent(getApplicationContext(), ProyGradActivity.class);
						startActivity(i);
					}
					else
					{

						AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  
						dialogo1.setTitle("No hay conexion");  
						dialogo1.setMessage("Se ha guardado tu evento para enviarlo despues"); 
						dialogo1.setPositiveButton("OK",null);
						dialogo1.create();
						dialogo1.show();
					}
				}
				else{


					AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);  
					dialogo1.setTitle("No hay conexion");  
					dialogo1.setMessage("Se ha guardado tu evento para enviarlo despues"); 
					dialogo1.setPositiveButton("OK",null);
					dialogo1.create();
					dialogo1.show();
				}
			}	
		}
	}

	private String dondeEstoy() {
		return ProyGradActivity.dondeEstoy;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		String progressDuracion = "0";
		if(progress<(100/8))
			progressDuracion = "0:15";
		else if(progress<2*(100/7))
			progressDuracion = "0:30";
		else if(progress<3*(100/7))
			progressDuracion = "0:45";
		else if(progress<4*(100/7))
			progressDuracion = "1:00";
		else if(progress<5*(100/7))
			progressDuracion = "1:15";
		else if(progress<=6*(100/7))
			progressDuracion = "1:30";
		else if(progress<100)
			progressDuracion = "1:45";
		else if(progress==100)
			progressDuracion = "2:00";
		
		//La duracion del evento en millis
		//2 horas tienen 7200000 millis
		duracionSeekBar = (7200000*progress)/100;
		textDuracion.setText(progressDuracion+" horas");
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
}
