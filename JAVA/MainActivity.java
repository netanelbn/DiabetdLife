
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);



        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);


        email = spUser.getString("user", "");

        btnEdit = (Button) findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, editdata.class));
            }
        });

        btnLogOut = (Button) findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = spUser.edit();
                editor.putString("loginFlag", "false");
                editor.commit();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        btnaskquestion = (Button) findViewById(R.id.btnaskquestion);
        btnaskquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, FrequentlyAskedQuestions.class));
            }
        });
        btnInsert = (Button) findViewById(R.id.btnInsertData);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, insert.class));
            }
        });
        btnEye = (Button) findViewById(R.id.btnEyeExam);
        btnEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, eye_examinations.class));
            }
        });
        btnMoglobin = (Button) findViewById(R.id.btnMoglobin);
        btnMoglobin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Tests_Moglobin.class));
            }
        });
        btnShowGraph = (Button) findViewById(R.id.btnShowGraph);
        btnShowGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000, pintent);
    }

    public void getReminderEye() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EyeExam");
        query.orderByDescending("updatedAt");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        ParseObject obj = scoreList.get(0);
                        SvcReminder.eyeExam = obj.getString("nextdate");
                    }
                } else {

                }
            }
        });
    }

    public void getReminderMoglobin() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MoglobinExam");
        query.orderByDescending("updatedAt");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() > 0) {
                        ParseObject obj = scoreList.get(0);
                        SvcReminder.moglobinExam = obj.getString("nextdate");
                    }
                } else {

                }
            }
        });
    }

    public void getReminderMeal() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");

        query.orderByDescending("updatedAt");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    ParseObject obj = scoreList.get(0);
                    if (!TextUtils.isEmpty(obj.getString("breakfasttime"))) {
                        SvcReminder.breakfast = getNextHour(obj.getString("breakfasttime"));
                        SvcReminder.breakfast_0 = getNextHour_0(obj.getString("breakfasttime"));
                    }

                    if (!TextUtils.isEmpty(obj.getString("lunchtime"))) {
                        SvcReminder.lunch = getNextHour(obj.getString("lunchtime"));
                        SvcReminder.lunch_0 = getNextHour_0(obj.getString("lunchtime"));
                    }

                    if (!TextUtils.isEmpty(obj.getString("dinnertime"))) {
                        SvcReminder.dinner = getNextHour(obj.getString("dinnertime"));
                        SvcReminder.dinner_0 = getNextHour_0(obj.getString("dinnertime"));
                    }
                } else {

                }
            }
        });
    }

    public static String getNextHour(String hour) {
        String[] strHour = hour.split(":");
        int nextHour = Integer.parseInt(strHour[0]) + 2;
        if (nextHour > 24) nextHour = nextHour - 24 + 1;
        if (nextHour < 10) return "0" + nextHour + ":" + strHour[1];
        return nextHour + ":" + strHour[1];
    }

    public static String getNextHour_0(String hour) {
        String[] strHour = hour.split(":");
        int nextHour = Integer.parseInt(strHour[0]);
        if (nextHour > 24) nextHour = nextHour - 24 + 1;
        if (nextHour < 10) return "0" + nextHour + ":" + strHour[1];
        return nextHour + ":" + strHour[1];
    }


    @Override
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