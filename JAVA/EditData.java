package com.parse.DiabetsApplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.SaveCallback;

import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class editdata extends ActionBarActivity {

    EditText etxtName, etxtPassword, etxtAddress, etxtBreakTime, etxtLunch, etxtDinner;
    Button btnEUpdate;
    ParseObject regObj;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editdata);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        email = spUser.getString("user", "");
        etxtName = (EditText) findViewById(R.id.etxtEName);
        etxtPassword = (EditText) findViewById(R.id.etxtEPassWord);
        etxtAddress = (EditText) findViewById(R.id.etxtEAddress);
        etxtBreakTime = (EditText) findViewById(R.id.etxtEBreak);
        SetTime setBreakfast = new SetTime(etxtBreakTime, editdata.this);
        etxtLunch = (EditText) findViewById(R.id.etxtELunch);
        SetTime setLunch = new SetTime(etxtLunch, editdata.this);
        etxtDinner = (EditText) findViewById(R.id.etxtEDinner);
        SetTime setDinner = new SetTime(etxtDinner, editdata.this);
        btnEUpdate = (Button) findViewById(R.id.btnEUpdate);



        FetchData();

        btnEUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regObj != null) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");
                    query.whereEqualTo("email", email);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(final ParseObject object, ParseException e) {
                            if (object == null) {
              
                                Log.d("score", "The getFirst request failed.");
                            } else {
                      
                                Log.d("score", "Retrieved the object.");
                                object.put("username", etxtName.getText().toString());
                                object.put("password", etxtPassword.getText().toString());
                                object.put("address", etxtAddress.getText().toString());
                                object.put("breakfasttime", etxtBreakTime.getText().toString());
                                object.put("lunchtime", etxtLunch.getText().toString());
                                object.put("dinnertime", etxtDinner.getText().toString());
                                object.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        Toast.makeText(editdata.this, "עדכון הפרטים בוצע בהצלחה!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

 
    void FetchData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    regObj = scoreList.get(0);
                    etxtName.setText(regObj.getString("username"));
                    etxtPassword.setText(regObj.getString("password"));
                    etxtAddress.setText(regObj.getString("address"));
                    etxtBreakTime.setText(regObj.getString("breakfasttime"));
                    etxtLunch.setText(regObj.getString("lunchtime"));
                    etxtDinner.setText(regObj.getString("dinnertime"));

                } else {

                }
            }
        });
    }


}
