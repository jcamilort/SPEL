package com.example.geoloc;

import com.example.proyectogrado.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSLocation extends Service{

	private double latitud;
	private double longitud;

	public static String GPS_CHANGED = "GPS CHANGED";
	public LocationManager locationManager=null; 
	public LocationFinder locFinder;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d("GPS_SERVICE", "Service Created");

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				latitud = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
				longitud = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();

				Intent i = new Intent(GPS_CHANGED);
				i.putExtra("latitud",latitud+"");
				i.putExtra("longitud",longitud+"");
				sendBroadcast(i);
				Log.d("GPS", "New reading");
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {

			}

			public void onProviderDisabled(String provider) {

			}
		};

		//Request para la actualizacion de locacion por GPS en el listener
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


		Log.d("GPS_SERVICE", "Service Available? "+isAvailable());
	}

	@Override
	public void onDestroy()
	{

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public double getLatitud()
	{
		return latitud;
	}
	public double getLongitud()
	{
		return longitud;
	}

	public boolean isAvailable()
	{
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	
	/**
	 * Metodo para mostrar una notificacion en el dispositivo
	 * @param notificationTitle - titulo de la notificacion
	 * @param notificationMessage - mensaje de la notificacion
	 */
	@SuppressWarnings("deprecation")
	private void notificar(String notificationTitle, String notificationMessage) 
	{
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				"Nueva Posicion", System.currentTimeMillis());

		Intent notificationIntent = new Intent(this, LocationFinder.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//notificationIntent.putExtra("url", url);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		//notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.setLatestEventInfo(this, notificationTitle,
				notificationMessage, pendingIntent);
		notificationManager.notify(9999, notification);
	}    
}
