/**
 * Author: Luis Sanchez
 */
package edu.utep.cs.cs4330.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class BootupReceiver extends BroadcastReceiver {

    /**
     * Broadcast receiver that runs code when device has finished booting up
     * @param context Context of the application
     * @param intent Intent object that contains android intents. Used to check if boot has completed.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "NOTIFICATION", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            String notificationTitle = intent.getStringExtra("notification");


            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
//
            int requestCode = intent.getIntExtra("requestCode", 0);
            String time = intent.getStringExtra("time");
            String title = intent.getStringExtra("title");
//
//            String[] splitArray = time.split("\\s+");
//            int month = Integer.parseInt(splitArray[1]);
//            int dayOfMonth = Integer.parseInt(splitArray[2]);
//            int hour = Integer.parseInt(splitArray[3]);
//            int minute = Integer.parseInt(splitArray[4]);
//
//            calendar.set(Calendar.MONTH, month);
//            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.MINUTE, minute);

//            context = ApplicationActivity.class;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, BootupReceiver.class);
            i.putExtra("notificationTitle", "Title");

            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode, i, 0);


            //ALWAYS recompute the calendar after using add, set, roll
            Date date = calendar.getTime();

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarmIntent);
        }
    }
}