package com.masspeoplealfa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class MainActivity extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		startService();
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
}
