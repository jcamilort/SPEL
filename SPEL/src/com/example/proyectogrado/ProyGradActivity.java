package com.example.proyectogrado;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.broadcasting.BroadcastConstants;
import com.example.geoloc.LocationFinder;
import com.example.testing.LocationFinderActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProyGradActivity extends Activity {
	public static String userName = "none";
	public static String nickName = "";
	private boolean check;
	private String datos;
	public static String dondeEstoy = "No Disponible";
	public static int counter=0;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(LocationFinder.LF_FOUND_LOC_GPS)){
				String latitud = intent.getExtras().getString("latitud");
				String longitud = intent.getExtras().getString("longitud");
				String lugar = intent.getExtras().getString("lugar");
				if(lugar==null)
				{
					lugar = "Fuera del campus";
				}

				if(longitud==null||latitud==null)
					dondeEstoy="No Disponible";
				else 
					dondeEstoy = lugar;
			}
			else if(action.equals(LocationFinder.LF_FOUND_LOC_BEACON)){
				String major = intent.getExtras().getString("majorbeacon");
				String minor = intent.getExtras().getString("minorbeacon");
				String lugar = intent.getExtras().getString("lugar");

				if(major==null||minor==null)
					dondeEstoy="No Disponible";
				else
					dondeEstoy = lugar;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO start services, register them in manifest
		super.onCreate(savedInstanceState);

		Intent queueIntent = new Intent(getApplicationContext(),
				LocationFinder.class);
		startService(queueIntent);

		IntentFilter filter = new IntentFilter();
		filter.addAction(LocationFinder.LF_FOUND_LOC_BEACON);
		filter.addAction(LocationFinder.LF_FOUND_LOC_GPS);
		registerReceiver(receiver, filter);

		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		userName = sharedPref.getString(getString(R.string.user_name), "none");
		counter = sharedPref.getInt("counter", 0);

		if (userName.equals("none") || userName == null || userName.equals("")) {
			setContentView(R.layout.activity_ask_username);
		} else {
			setContentView(R.layout.activity_proy_grad);
			TextView txt = (TextView) findViewById(R.id.textHola);
			txt.setText("Hola: " + userName + "!");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.proy_grad, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_nuevo_evento:
			Intent intent = new Intent(this, CrearEventoActivity.class);
			startActivity(intent);
			Toast.makeText(this, "Quiero crear un nuevo evento",
					Toast.LENGTH_SHORT).show();
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

	public void onLogin(View view) {
		try {
			EditText userLogin = (EditText) findViewById(R.id.editTextLogin);
			EditText userPassword = (EditText) findViewById(R.id.editTextPassword);
			EditText nickname = (EditText) findViewById(R.id.editTextNickName);

			String userLoginString = userLogin.getText().toString();
			String userPasswordString = userPassword.getText().toString();
			String nicknameString = nickname.getText().toString();
			
			userLoginString = userLoginString.trim();

			check = autenticar(userLoginString, userPasswordString);

			System.out.println("_______Credenciales: " + userLoginString);
			if (userLoginString.replace(" ", "").equals("")
					|| userLoginString == null || userPasswordString.equals("")
					|| userPasswordString == null) {
				Toast.makeText(this, "Ingresa tus datos", Toast.LENGTH_SHORT)
						.show();
				System.out.println("_______Credenciales: Vacios");
			} else if (check) {
				datos = new TaskDatosUser().execute(userName,
						userPasswordString).get();

				if (datos.equals("ERROR")) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Error obteniendo datos del usuario",
							Toast.LENGTH_SHORT);
					toast.show();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Bienvenido " + datos, Toast.LENGTH_SHORT);
					toast.show();

					userName = userLoginString;
					nickName = nicknameString;
				}
				System.out.println("_______Credenciales: validos");

				SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.user_name), userLoginString);
				editor.putInt("counter", counter);
				editor.commit();

				this.recreate();
			} else {
				Toast toast = Toast
						.makeText(
								getApplicationContext(),
								"Contrasenha o nombre de usuario no valido. Intentalo de nuevo",
								Toast.LENGTH_LONG);
				toast.show();
			}
		} catch (Exception e) {
			Log.e("Registro Activity", e.getMessage());
		}

	}

	public boolean autenticar(String user, String password) {
		try {
			return new TaskAutenticar().execute(user, password).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e("LDAP_AUTH", "InterruptedException: "+e.getMessage());
		} catch (ExecutionException e) {
			Log.e("LDAP_AUTH", "ExecutionException: "+e.getMessage());
		}

		return false;
	}

	/**
	 * AsyncTask para autenticar al usuario con LDAP
	 * 
	 * @author AndresFelipe
	 */
	private class TaskAutenticar extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String user = params[0];
			String password = params[1];

			JSONObject response = receiveJSON(
					"http://157.253.236.124/SIEGE/authenticate.php",
					"u=" + URLEncoder.encode(user) + "&p="
							+ URLEncoder.encode(password));
			try {
				return response.getBoolean("r");
			} catch (JSONException e) {
				
				return false;
			}
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

	/**
	 * AsyncTask para obtener datos del usuario
	 */
	private class TaskDatosUser extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String username = params[0];
			String passwd = params[1];

			JSONObject response = receiveJSON(
					"http://157.253.236.124/SIEGE/authenticate.php",
					"u=" + URLEncoder.encode(username) + "&p="
							+ URLEncoder.encode(passwd));

			try {
				return response.getString("displayname");
			} catch (JSONException e) {
				return "ERROR";
			}
		}
	}
}
