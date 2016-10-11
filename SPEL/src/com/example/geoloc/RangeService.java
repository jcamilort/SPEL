package com.example.geoloc;

import java.util.Collection;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class RangeService extends Service implements BeaconConsumer{

	private BeaconManager beaconManager;

	public static String BEACON_READ = "NEW BEACON";
	
	@Override
	public void onCreate()
	{
		Log.d("RANGING_SERVICE", "Service OnCreate");
		super.onCreate();
		
		beaconManager = BeaconManager.getInstanceForApplication(this);
		Log.d("RANGING_SERVICE", "Service Created");
		
		if((getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)))
		{
			beaconManager.setBackgroundBetweenScanPeriod(120000);
			beaconManager.setBackgroundScanPeriod(30000);
			beaconManager.bind(this);
			Log.d("RANGING_SERVICE", "Device Suported");
		}
		else
		{
			stopSelf();
			Log.d("RANGING_SERVICE", "Device Not Suported");
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		beaconManager.unbind(this);
	}
    

	@Override
	public void onBeaconServiceConnect() 
	{
		Log.d("RangingService", "Entro a onIBeaconServiceConnect para empezar a mirar los beacons");
		
		beaconManager.setRangeNotifier(new RangeNotifier() {
			
			@Override
			public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region)
			{
				if(beacons.size() > 0)
				{
					Beacon masCercano = beacons.iterator().next();
					
					for(Beacon b : beacons)
					{
						if(b.getDistance()< masCercano.getDistance())
						{
							masCercano = b;
						}
					}

					Intent i = new Intent(BEACON_READ);
					i.putExtra("major",masCercano.getId2().toInt()+"");
					i.putExtra("minor",masCercano.getId3().toInt()+"");
					sendBroadcast(i);
					Log.d("RangingService","Beacon leido "+masCercano.getId2().toInt()+";"+masCercano.getId3().toInt());
				}
				else
				{
					Log.d("RangingService","No hay beacons");
				}
			}
		});
		
		try
		{
			Log.i("RangingService", "Entro a startRangingBeacons");
			beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
		}
		catch(Exception e)
		{
			Log.e("RangingService", "Error al iniciar escaneo: "+e.getMessage());
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d("RangingService", "Entra a onBind");
		return null;
	}
}
