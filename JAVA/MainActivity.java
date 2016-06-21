
package com.parse.DiabetsApplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    //variables Declaration
    public static AlarmManager alarm;
    public static PendingIntent pintent;
    SharedPreferences.Editor editor;
    String email;
    Button btnInsert, btnEye, btnMoglobin, btnShowGraph, btnLogOut, btnEdit, btnaskquestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Connection between XML component to JAVA class
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //Sharedpreferences is an effective and convenient mechanism for saving data.
        // The data are saved in pairs key / value.
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);


        email = spUser.getString("user", "");

        //Connection between XML component to JAVA class
        btnEdit = (Button) findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go from the main to editdata
                startActivity(new Intent(MainActivity.this, editdata.class));
            }
        });

        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Puts the user's data in a file "USER"
                editor = spUser.edit();
                editor.putString("loginFlag", "false");

                //Updating the data in file
                editor.commit();


                //go from the main to Login
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        btnaskquestion = (Button) findViewById(R.id.btnaskquestion);
        btnaskquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go from the main to requentlyAskedQuestions
                startActivity(new Intent(MainActivity.this, FrequentlyAskedQuestions.class));
            }
        });
        btnInsert = (Button) findViewById(R.id.btnInsertData);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go from the main to insert

                startActivity(new Intent(MainActivity.this, insert.class));
            }
        });
        btnEye = (Button) findViewById(R.id.btnEyeExam);
        btnEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go from the main to eye_examinations
                startActivity(new Intent(MainActivity.this, eye_examinations.class));
            }
        });
        btnMoglobin = (Button) findViewById(R.id.btnMoglobin);
        btnMoglobin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go from the main to Tests_Moglobin
                startActivity(new Intent(MainActivity.this, Tests_Moglobin.class));
            }
        });
        btnShowGraph = (Button) findViewById(R.id.btnShowGraph);
        btnShowGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go from the main to graph
                startActivity(new Intent(MainActivity.this, graph.class));
            }
        });

        try {
            startService();
        } catch (Exception ae) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }


    }

    public void startService() {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);
        Intent intent = new Intent(MainActivity.this, SvcReminder.class);
        //The goal is to create an object PendingIntent, which is intent with another cover, designed for operation by another module.
        pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        //-Alarm operator the Service
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //.RTC_WAKEUP = "Real Time clock" The "WAKEUP" will trigger the signal even if the system is in leep.
        // create a circular Alarm (snooze) at predetermined intervals
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000, pintent);
    }

    public void getReminderEye() {
        //go to table "EyeExam" in parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EyeExam");
        //Sort by date
        query.orderByDescending("updatedAt");
        //go to col email in parse
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        ParseObject obj = scoreList.get(0);
                        //send to SvcReminder the nextdate of EyeExam
                        SvcReminder.eyeExam = obj.getString("nextdate");
                    }
                } else {

                }
            }
        });
    }

    public void getReminderMoglobin() {
        //go to table "MoglobinExam" in parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MoglobinExam");
        //Sort by date
        query.orderByDescending("updatedAt");
        //go to col email in parse
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        ParseObject obj = scoreList.get(0);
                        //send to SvcReminder the nextdate of "MoglobinExam"
                        SvcReminder.moglobinExam = obj.getString("nextdate");
                    }
                } else {

                }
            }
        });
    }

    public void getReminderMeal() {
        //go to table "Registers" in parse

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");
        //Sort by date

        query.orderByDescending("updatedAt");
        //go to col email in parse

        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    ParseObject obj = scoreList.get(0);
                    if (!TextUtils.isEmpty(obj.getString("breakfasttime"))) {
                        //send 2 time of breakfast to SvcReminder: real time and 2 hours after breakfast
                        SvcReminder.breakfast = getNextHour(obj.getString("breakfasttime"));
                        SvcReminder.breakfast_0 = getNextHour_0(obj.getString("breakfasttime"));
                    }

                    if (!TextUtils.isEmpty(obj.getString("lunchtime"))) {
                        //send 2 time of lunch to SvcReminder: real time and 2 hours after lunch
                        SvcReminder.lunch = getNextHour(obj.getString("lunchtime"));
                        SvcReminder.lunch_0 = getNextHour_0(obj.getString("lunchtime"));
                    }

                    if (!TextUtils.isEmpty(obj.getString("dinnertime"))) {
                        //send 2 time of dinner to SvcReminder: real time and 2 hours after dinner
                        SvcReminder.dinner = getNextHour(obj.getString("dinnertime"));
                        SvcReminder.dinner_0 = getNextHour_0(obj.getString("dinnertime"));
                    }
                } else {

                }
            }
        });
    }

    //method that calculates and split the string two hours after meal time
    public static String getNextHour(String hour) {
        String[] strHour = hour.split(":");
        int nextHour = Integer.parseInt(strHour[0]) + 2;
        //case the hour is 23:00--->25:00
        if (nextHour > 24) nextHour = nextHour - 24 + 1;
        if (nextHour < 10) return "0" + nextHour + ":" + strHour[1];
        return nextHour + ":" + strHour[1];
    }

    //method that calculates and split the string in the meal time
    public static String getNextHour_0(String hour) {
        String[] strHour = hour.split(":");
        int nextHour = Integer.parseInt(strHour[0]);
        //case the hour is 23:00--->25:00
        if (nextHour > 24) nextHour = nextHour - 24 + 1;
        if (nextHour < 10) return "0" + nextHour + ":" + strHour[1];
        return nextHour + ":" + strHour[1];
    }


    @Override
    //the activity life cycle can come from thr start or from the pause to activity
    protected void onResume() {
        super.onResume();

        try {
            getReminderEye();
        } catch (Exception ae) {
            Toast.makeText(this, "getReminderEye", Toast.LENGTH_LONG).show();
        }
        try {
            getReminderMoglobin();
        } catch (Exception ae) {
            Toast.makeText(this, "getReminderMoglobin", Toast.LENGTH_LONG).show();

        }
        try {
            getReminderMeal();
        } catch (Exception ae) {
            Toast.makeText(this, "getReminderMeal", Toast.LENGTH_LONG).show();
        }

        try {
            startService();
        } catch (Exception ae) {
            Toast.makeText(this, "Error in service", Toast.LENGTH_LONG).show();
        }

    }


}