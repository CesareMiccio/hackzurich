package com.masspeoplealfa;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.google.android.gms.common.*;
import com.google.android.gms.gcm.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity 
{

	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "0.0.0.0.1";	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private final static String SENDER_ID = "582951463448";
	
	static final String TAG = "GCMDemo";
	
	GoogleCloudMessaging gcm;
	Context context;
	
	String regid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		startService();
		
		if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            //regid = getRegistrationId(context);

            //if (regid.isEmpty()) {
                registerInBackground();
            //}
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
	}

	void startService()
	{
		startService(new Intent(this, TimerService.class));
	}
	
	@Override
	public void onBackPressed()
	{
		stopService(new Intent(this, TimerService.class));
		super.onDestroy();
		System.exit(0);
	}
	public void checkConnection()
	{
		if(isNetworkAvailable() == true)
		{
			startService();
		}
		else
		{
			final AlertDialog.Builder alertConnection = new AlertDialog.Builder(this);
			alertConnection.setTitle("Connection");
			alertConnection.setMessage("Do you want to open the connection data?");
			alertConnection.setCancelable(false);
			alertConnection.setPositiveButton("YES",  new DialogInterface.OnClickListener() 
			{
				  public void onClick(DialogInterface dialog, int id) 
				  {
					  Intent intent = new Intent(Intent.ACTION_MAIN);
					  intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
					  startActivity(intent);
				  }
			});
			alertConnection.setNegativeButton("NO", new DialogInterface.OnClickListener() 
			{				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{					
					// TODO Auto-generated method stub
					return;
				}				
			});
			alertConnection.create();
			alertConnection.show();
		}
	}
	private boolean isNetworkAvailable() 
	{  
	    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();  
	    return activeNetworkInfo != null;  
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i("services", "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		return 4;
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
	    new AsyncTask<Void,Void,String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	        	
	        	Log.i("test","test register");
	        	
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;

	                Log.i("info","informazioni"+msg);
	                
	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                String path = "http://masspeople.herokuapp.com/user";
	    			
	    			//Toast avviso=Toast.makeText(this, errorToast, Toast.LENGTH_LONG);
	    			
	    			HttpClient client = new DefaultHttpClient();
	    			HttpPost post = new HttpPost(path);
	    			
	    			//ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
	    			//NetworkInfo info = cm.getActiveNetworkInfo();
	    			


	    		    //passes the results to a string builder/entity
	    		    StringEntity se = new StringEntity("{\"name\": \"andrea\", \"registrationId\": \""+regid+"\", \"email\": \"andrea@wavein.ch\"}");

	    		    //sets the post request as the resulting string
	    		    post.setEntity(se);
	    		    //sets a request header so the page receving the request
	    		    //will know what to do with it
	    		    post.setHeader("Accept", "application/json");
	    		    post.setHeader("Content-type", "application/json");


	    			try
	    			{
	    				client.execute(post);
	    			
	    			} catch(ClientProtocolException e){
	    				// TODO Auto-genereted catch block
	    				Log.e("error","error"+e.getMessage());
	    			} catch(IOException e){
	    				// TODO Auto-genereted catch block
	    				Log.e("error","error"+e.getMessage());
	    			}

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                Log.e("test","test"+msg);
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            //mDisplay.append(msg + "\n");
	        }


	    }.execute(null, null, null);

	    
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
}
