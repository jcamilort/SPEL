package com.example.eventlifecycle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mundo.Evento;
import com.example.proyectogrado.ProyGradActivity;

import android.os.AsyncTask;
import android.util.Log;

public class EventRegistration {
	
	public static EventRegistration sInstance = null;
	//TODO
	public final static String SIEGE_SERVER_URI = "http://157.253.236.124/SIEGE/sreceiver.php";
	
	public EventRegistration()
	{
		sInstance = this;
	}
	
	public static EventRegistration getInstance()
	{
		if(sInstance!=null)
			return sInstance;
		else
			return new EventRegistration();
	}
	

	public boolean sendToServer(Evento event) {
		try {
			return new TaskRegistrarEvento().execute(event.getTitle(),
					event.getDescription(), event.getStartTime() + "",
					event.getEndTime() + "", event.getLocation()).get();
		} catch (InterruptedException e) {
			Log.e("EVENT REGISTRATION", "Interrupted");
			return false;
		} catch (ExecutionException e) {
			Log.e("EVENT REGISTRATION", "Not Executed");
			return false;
		}
	}

	public boolean validateEvent(Evento event, boolean validate) {
		return false;
	}

	/**
	 * AsyncTask para registrar el evento en el servidor
	 * 
	 * @author AndresFelipe
	 */
	private class TaskRegistrarEvento extends AsyncTask<String, Void, Boolean> {

		private static final String SOCKET_URL = "157.253.226.15";

		@Override
		protected Boolean doInBackground(String... params) {
			String title = params[0];
			String description = params[1];
			String starttime = params[2];
			String endtime = params[3];
			String location = params[4];
			try {
				Socket sock = new Socket(SOCKET_URL, 7070, true);
				BufferedReader br;
				if(sock.isConnected()){
					PrintWriter pw = new PrintWriter(sock.getOutputStream());
					br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					/**String mensaje = "t=" + URLEncoder.encode(title) + "&d="
							+ URLEncoder.encode(description)+ "&st="
							+ URLEncoder.encode(starttime)+ "&et="
									+ URLEncoder.encode(endtime)+ "&l="
											+ URLEncoder.encode(location)+ "&uid="
													+ URLEncoder.encode(ProyGradActivity.userName+ProyGradActivity.counter) ;
					*/
					String mensaje = "Holiwi!";
					pw.print(mensaje);
					return true;
				}
				else
				{
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			/**
			JSONObject response = receiveJSON(
					SIEGE_SERVER_URI,
					"t=" + URLEncoder.encode(title) + "&d="
							+ URLEncoder.encode(description)+ "&st="
									+ URLEncoder.encode(starttime)+ "&et="
											+ URLEncoder.encode(endtime)+ "&l="
													+ URLEncoder.encode(location)+ "&uid="
															+ URLEncoder.encode(ProyGradActivity.userName+ProyGradActivity.counter) );
			
			
			try {
				return response.getBoolean("r");
			} catch (JSONException e) {

				return false;
			}*/
		}
	}

	public JSONObject receiveJSON(String url, String params) {
		BufferedReader in = null;
		String textv = "";

		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			URI website = new URI(url + "?" + params);
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			// NEW CODE
			String line = in.readLine();
			textv += line;
			// END OF NEW CODE
			return new JSONObject(textv);
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
			return null;
		}
	}
}
