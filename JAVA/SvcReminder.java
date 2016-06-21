package com.parse.DiabetsApplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SvcReminder extends Service {


    //variables Declaration
//    public static int counter = 0;
    public static String eyeExam = "";
    public static String moglobinExam = "";
    public static String breakfast = "";
    public static String lunch = "";
    public static String dinner = "";
    public static String breakfast_0 = "";
    public static String lunch_0 = "";
    public static String dinner_0 = "";
    public static boolean isEye = true;
    public static boolean isMog = true;
    public static boolean isBreak = true;
    public static boolean isLunch = true;
    public static boolean isDinner = true;
    public static boolean isBreak_0 = true;
    public static boolean isLunch_0 = true;
    public static boolean isDinner_0 = true;

    public SvcReminder() {

    }

    @Override
    //bind the intent that comming from the mainActivity
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "First Service was Created", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //  counter++;

//Method that build a format of hour for us to compare between the server and know when to send reminders

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = format.format(new Date());
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String strHour = hour + "";
        if (hour < 10) strHour = "0" + hour;
        String strMinuter = minute + "";
        if (minute < 10) strMinuter = "0" + minute;
        String formatedTime = strHour + ":" + strMinuter;


        //if the time of breakfast and 2 hours next equals to the real time-formatedTime
        if (breakfast.equals(formatedTime) && isBreak) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת לבדיקת סוכר!";
            Reminder.body = "אכלת? בדקת!";
            startActivity(myintent);
            isBreak = false;
        }

        //if the time of breakfast equals to the real time-formatedTime
        if (breakfast_0.equals(formatedTime) && isBreak_0) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת לבדיקת סוכר!";
            Reminder.body = "אכלת? בדקת!";
            startActivity(myintent);
            isBreak_0 = false;
        }

        //if the time of lunch and 2 hours next equals to the real time-formatedTime
        if (lunch.equals(formatedTime) && isLunch) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת לבדיקת סוכר!";
            Reminder.body = "אכלת? בדקת!";
            startActivity(myintent);
            isLunch = false;
        }

        //if the time of lunch equals to the real time-formatedTime
        if (lunch_0.equals(formatedTime) && isLunch_0) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת לבדיקת סוכר!";
            Reminder.body = "אכלת? בדקת!";
            startActivity(myintent);
            isLunch_0 = false;
        }

        //if the time of dinner and 2 hours next equals to the real time-formatedTime
        if (dinner.equals(formatedTime) && isDinner) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת לבדיקת סוכר!";
            Reminder.body = "אכלת? בדקת!";
            startActivity(myintent);
            isDinner = false;
        }

        //if the time of dinner equals to the real time-formatedTime
        if (dinner_0.equals(formatedTime) && isDinner_0) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת לבדיקת סוכר!";
            Reminder.body = "אכלת? בדקת!";
            startActivity(myintent);
            isDinner_0 = false;
        }

        //if the time of eyeExam equals to the real time-formatedDate
        if (eyeExam.equals(formattedDate) && isEye) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת!";
            Reminder.body = "הגיע הזמן לעשות בדיקת עיניים!";
            startActivity(myintent);
            isEye = false;
        }

        //if the time of moglobinExam equals to the real time-formatedDate
        if (moglobinExam.equals(formattedDate) && isMog) {
            Intent myintent = new Intent(this, Reminder.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Reminder.title = "תזכורת!";
            Reminder.body = "הגיע הזמן לעשות בדיקת A1C";
            startActivity(myintent);
            isMog = false;
        }

        //Makes all the flags true before the next day
        if (hour == 23 && minute == 58) {
            isBreak = true;
            isLunch = true;
            isDinner = true;
            isBreak_0 = true;
            isLunch_0 = true;
            isDinner_0 = true;
            isEye = true;
            isMog = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {


    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    public void onTaskRemoved(Intent rootIntent) {
        //Cancel the Alarm
        MainActivity.alarm.cancel(MainActivity.pintent);
        this.stopSelf();
    }
}
