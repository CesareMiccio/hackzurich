package com.masspeoplealfa;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.*;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        
        //Do something here with the notifications
        Log.i("notification","notification");
        
        Notification.Builder mBuilder =
		        new Notification.Builder(context)
        		.setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Get the promo!")
		        .setContentText("Go to Bar XX");
		// Creates an explicit intent for an Activity in your app
		//Intent resultIntent = new Intent(this, ResultActivity.class);

//		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(999, mBuilder.build());
    }
}