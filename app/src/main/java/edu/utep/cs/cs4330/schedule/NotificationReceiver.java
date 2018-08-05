/**
 * Author: Luis Sanchez
 */

package edu.utep.cs.cs4330.schedule;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationReceiver extends WakefulBroadcastReceiver {
    private AlarmManager AlarmManager;

    /**
     * Receives an intent to display a notification
     * @param context Application's context
     * @param i the intent being received
     */
    public void onReceive(Context context, Intent i) {

        NotificationReceiver.completeWakefulIntent(i);
        NotificationManager nm = (NotificationManager) context.getSystemService
                (NOTIFICATION_SERVICE);
        NotificationCompat.Builder notifBuilder;

        String notificationTxt = i.getStringExtra("notification");
        notifBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Task")
                .setContentText(notificationTxt);
        nm.notify(1, notifBuilder.build());
    }


    /**
     *  Schedules a pending intent to be run
     * @param context the application's context
     * @param note note for which the notification will be set
     */
    public void addAlarm(Context context, Note note) {
        String time = note.getTime();
        String title = note.getTitle();
        int requestCode = note.getId();

        AlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String[] splitArray = time.split("\\s+");
        int month = Integer.parseInt(splitArray[1]) - 1;
        int dayOfMonth = Integer.parseInt(splitArray[2]);
        int hour = Integer.parseInt(splitArray[3]);
        int minute = Integer.parseInt(splitArray[4]);

        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notification", title);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("time", time);
        intent.putExtra("title", title);

        PendingIntent alarm = PendingIntent.getBroadcast(context, requestCode, intent, 0);

        Date date = calendar.getTime();

        AlarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), alarm);
    }

    /**
     *  Cancels a pending intent to be run
     * @param context the application's context
     * @param note note containing id, id is used for requestCode
     */
    public void deleteAlarm(Context context, Note note) {
        Log.d("WakefulAlarmReceiver", "{cancelAlarm}");

        AlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent alarm = PendingIntent.getBroadcast(context, note.getId(), intent, 0);

        AlarmManager.cancel(alarm);
    }
}