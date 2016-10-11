package com.example.geoloc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


public class LocationFinder extends Service{
	
	
	public final static String URL_BASE_MAP_GPS = "https://www.googleapis.com/fusiontables/v1/query?sql=SELECT%20name%20FROM%201w_pgiGr0SJ-Fev-qroKdwh19_eptp4E8pJvDTFhq%20WHERE%20ST_INTERSECTS%28geometry,CIRCLE%28LATLNG%28";
	public final static String MAP_GPS_KEY = "%29,35%29%29&key=AIzaSyAm9yWCV7JPCTHCJut8whOjARd7pwROFDQ";
	public final static String LF_FOUND_LOC_GPS="LOCFOUND BY GPS";
	public final static String LF_FOUND_LOC_BEACON="LOCFOUND BY BEACON";
	//---------------------------------------------------------------------------
	//Atributos
	//---------------------------------------------------------------------------
	
	private ReceiverLocationFinder receiver;
	
	//---------------------------------------------------------------------------
	//Metodos
	//---------------------------------------------------------------------------
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		receiver = new ReceiverLocationFinder();
		

		Intent queueIntent = new Intent(getApplicationContext(),GPSLocation.class);
		Intent queueIntent2 = new Intent(getApplicationContext(),RangeService.class);
		
		startService(queueIntent);
		startService(queueIntent2);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(GPSLocation.GPS_CHANGED);
		filter.addAction(RangeService.BEACON_READ);
		registerReceiver(receiver, filter);
		
		Log.d("Location Finder","Location finder creado");
		/**
		//COMO TEST PARA LA FUNCIONALIDAD DE LA APP
		Intent i = new Intent("Prueba");
		i.putExtra("Major", "10");
		i.putExtra("Minor", "15");
		i.setAction(RangeService.BEACON_READ);
		sendBroadcast(i);
		Log.d("TEST UNIANDESAC", "Envio broadcast de prueba. "+i.getAction());*/
	}
		
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class ReceiverLocationFinder extends BroadcastReceiver 
	{
		public ReceiverLocationFinder()
		{
		
		}
		
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if(intent.getAction().equals(RangeService.BEACON_READ))
			{
				Log.d("Location Finder", "Recibo broadcast de beacon service");
				
				String major = intent.getStringExtra("Major");
				String minor = intent.getStringExtra("Minor");
				String distancia = intent.getStringExtra("Distancia");
				
				Log.d("Location Finder", "Major: "+major+" Minor: "+minor+" Distance: "+distancia);
				//Consulta al servidor de mapas y broadcast del lugar encontrado
				String lugarEncontrado="";
				try {
					lugarEncontrado = new ConsultaBeaconTask().execute("BEACONS",major,minor).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				Log.i("LocationFinder", lugarEncontrado);
				Intent i = new Intent("FoundPlace");
				i.putExtra("lugar", lugarEncontrado);
				i.putExtra("distancia", distancia);
				i.putExtra("major", major);
				i.putExtra("minor", minor);
				i.setAction(LF_FOUND_LOC_BEACON);
				sendBroadcast(i);
				
				Log.d("Location Finder","Envio broadcast de lugar a partir de beacon."+"\n"+
										"Lugar: "+lugarEncontrado+" Major: "+major+ " Minor: "+minor);
			}
			else if(intent.getAction().equals(GPSLocation.GPS_CHANGED))
			{
				Log.d("Location Finder", "Recibo broadcast de GPS");
				
				String longitud = intent.getStringExtra("longitud");
				String latitud = intent.getStringExtra("latitud");
				
				//Consulta al servidor de mapas y broadcast del lugar
				
				String lugarEncontrado="";
				try {
					lugarEncontrado = new ConsultaGPSTask().execute("GPS",latitud,longitud).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				Intent i = new Intent("Found Place");
				i.putExtra("lugar", lugarEncontrado);
				i.putExtra("longitud", longitud);
				i.putExtra("latitud", latitud);
				i.setAction(LF_FOUND_LOC_GPS);
				sendBroadcast(i);
				
				Log.d("Location Finder","Envio broadcast de lugar a partir de GPS."+"\n"+
						"Lugar: "+lugarEncontrado+" Latitud: "+latitud+ " Longitud: "+longitud);
			}
		}
	}
	
	private class ConsultaBeaconTask extends AsyncTask<String,Void,String>
	{

		@Override
		protected String doInBackground(String... params) {
			
			String tipoRequest = params[0];
			
			if(tipoRequest.equals("BEACONS"))
			{
				String major = params[1];
				String minor = params[2];
				
				try
				{
					Socket s = new Socket("157.253.212.225",7070);
					PrintWriter out = new PrintWriter(s.getOutputStream(),true);
					BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
					
					String pedido = "BEACONS;"+major+";"+minor;
					
					out.println(pedido);
					
					String respuesta = in.readLine();
					
					if(respuesta == null)
					{
						return null;
					}
					else
					{
						return respuesta;
					}
				}
				catch(IOException e)
				{
					Log.e("TaskLocation","Error: "+e.getMessage());
				}
			}
			
			return null;
		}	
	}
	
	private class ConsultaGPSTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... params) 
		{
			try
			{
				String tipoRequest = params[0];

				if(tipoRequest.equals("GPS"))
				{
					String latitud = params[1];
					String longitud = params[2];

					String url = URL_BASE_MAP_GPS+latitud+","+longitud+MAP_GPS_KEY;

					JSONObject json = receiveJSON(url);
					
					String rta = json.getJSONArray("rows").get(0).toString();
					rta = rta.replace("[", "");
					rta = rta.replace("]", "");
					rta = rta.replace("\"", "");

					return rta;
				}
			}
			catch(Exception e)
			{
				Log.e("LocationFinder",e.getMessage());
			}
			return null;
		}
		
	}
	
	public JSONObject receiveJSON(String url) 
	{
		BufferedReader in = null;
		String textv = ""; 

		try
		{
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			URI website = new URI(url);
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);
			in = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			// NEW CODE
			String line;
			
			while((line=in.readLine()) != null)
			{
				textv += line;
			}
			// END OF NEW CODE
			
			System.out.println(textv);
			return new JSONObject(textv);
		}
		catch(Exception e)
		{
			Log.e("log_tag", "Error in http connection "+e.toString());
			return null;
		}
	}
}
