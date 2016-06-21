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

//Class responsible for updating user information and inserting it into the server PARSE

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

        //Sharedpreferences is an effective and convenient mechanism for saving data.
        // The data are saved in pairs key / value.
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        //Connection between XML component to JAVA class
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

        //method that takes the data  from PARSE and places them in the background app
        //  The user's information go right into the app. In case its a user: data fields will be empty

        FetchData();

        //Method tells us what happens when you click on the update button Details
        btnEUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (regObj != null) {

                    //go to table "Registers" in parse
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");
                    //go to col "email"  in parse
                    query.whereEqualTo("email", email);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(final ParseObject object, ParseException e) {
                            if (object == null) {
                                // Message to debag
                                Log.d("score", "The getFirst request failed.");
                            } else {
                                // Message to debag
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

    //method that takes the data  from PARSE and places them in the background app
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
