package com.masspeoplealfa;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class TimerService extends Service
{
	private static Timer timer = new Timer(); 
    public static String latitude, longitude;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    

    public IBinder onBind(Intent arg0) 
    {
          return null;
    }

    public void onCreate() 
    {
          super.onCreate();
          startService();
    }

    
    private void startService()
    {           
		timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
		obtainCoordinateGps();
		updateLocationGps();
    }

    private class mainTask extends TimerTask
    { 
        public void run() 
        {
        	new SendDataTask().execute();
            toastHandler.sendEmptyMessage(0);
            //obtainCoordinateGps();
        }
    }


    public void onDestroy() 
    {
          super.onDestroy();
          Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
          timer.cancel();
    }

    private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
        }
    };
    
    /*Obtain the coordinates with the LocationManager. If the gps's coordinates there aren't come to us, the return will be a predefined coordinates
     * in this case will be Lat(0.00000) and Lon(0.00000)*/
	public void obtainCoordinateGps()
	{		
		String errorToast = "Segnale GPS non pervenuto";
		LocationManager mymanager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Toast avviso=Toast.makeText(this, errorToast, Toast.LENGTH_LONG);
		try
		{
			double Lat=mymanager.getLastKnownLocation("gps").getLatitude();
			double Long=mymanager.getLastKnownLocation("gps").getLongitude();
		
			String Latitudine=Double.toString(Lat);
			String Longitudine=Double.toString(Long);
			
				latitude = (Latitudine);
				longitude = (Longitudine);
				Toast latitudine=Toast.makeText(this, "Lat " + Latitudine, Toast.LENGTH_LONG);
				Toast longitudine=Toast.makeText(this, "Long " + Longitudine, Toast.LENGTH_LONG);

			latitudine.show();
			longitudine.show();
			
		}
		catch(Exception e)
		{	
			latitude = ("&la=0.00000");
			longitude = ("&lo=0.00000");	
			avviso.show();
		}			
	}
	
	//Here there is the update of coordinates every 5000ms 
	public void updateLocationGps()
	{		
		
		Toast prova =Toast.makeText(this, "updateLGPS ", Toast.LENGTH_LONG);
		prova.show();
		
		LocationManager mymanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);		
		
		LocationListener myListener = new LocationListener()
		{			
			@Override
			public void onProviderDisabled(String arg0) 
			{
 				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String arg0) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) 
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onLocationChanged(Location location) 
			{
				// TODO Auto-generated method stub
			}
		};
			
		mymanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, myListener);
		
	}
	public String idTelephone()
	{
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String uId = tManager.getDeviceId();
		
		return (uId);
	}
	
	class SendDataTask extends AsyncTask<Void, Void, Void> {

	    private Exception exception;

	    protected Void doInBackground(Void... params) {
	    		
	    	
	    	String path = "http://masspeople.herokuapp.com/user/position/andrea@wavein.ch";
			String errorToast = "Richiesta non Inviata";
			
			path = (path+"/"+longitude+"/"+latitude);
			//Toast avviso=Toast.makeText(this, errorToast, Toast.LENGTH_LONG);
			
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(path);
			
			//ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			//NetworkInfo info = cm.getActiveNetworkInfo();
			

			try
			{
				client.execute(get);
			
			} catch(ClientProtocolException e){
				// TODO Auto-genereted catch block
				Log.e("test","test5"+e.getMessage());
			} catch(IOException e){
				// TODO Auto-genereted catch block
				Log.e("test","test6"+e.getMessage());
			}
			
	        	return null;
	        
	    }

	    protected void onPostExecute() {
	        // TODO: check this.exception 
	        // TODO: do something with the feed
	    }
	}
	
	
	public void starting()
	{		
		final Handler handler = new Handler();
		final int time = 10000;
			final Runnable runnable = new Runnable() 
			{
			
				@Override
				public void run()
				{					
                    new SendDataTask().execute();
                    handler.postDelayed(this, time);           		
				}
				
			};runnable.run();
	}
	

}