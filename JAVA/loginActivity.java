package com.parse.DiabetsApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;


public class LoginActivity extends ActionBarActivity {
    // Progress Dialog

    private String username, email, loginFlag;
    EditText etxtName, etxtEmail;
    CheckBox chkRemember;
    Button btnForget, btnRegister;
    public static String emailid;

    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //StrictMode is a developer tool which detects things you might be doing by accident and
        //brings them to your attention so you can fix them. StrictMode is most commonly used to catch
        //accidental disk or network access on the application's main thread, where UI operations are
        //received and animations take place

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Connection between XML component to JAVA class
        setContentView(R.layout.layout_login);
        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtEmail = (EditText) findViewById(R.id.etxtEmail);

        //Sharedpreferences is an effective and convenient mechanism for saving data.
        // The data are saved in pairs key / value.
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        username = spUser.getString("user", "");
        email = spUser.getString("email", "");
        loginFlag = spUser.getString("loginFlag", "");

        //If the user is really registered and the user return to the app
        if (loginFlag.equals("true")) {
            //Passes from the login screen to the menu screen by intent
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        etxtName.setText(username);
        etxtEmail.setText(email);


        //Connection between XML component to JAVA class
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etxtName.getText().toString();
                email = etxtEmail.getText().toString();
                if (chkRemember.isChecked()) {
                    //Saves the user's data in case the user will connect to the app again the app will remmber that
                    editor = spUser.edit();
                    editor.putString("user", username);
                    editor.putString("email", email);
                    editor.commit();
                }
                //go to table "Registers" in parse server
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");

                //go to table "Registers" and to col "email" doing the eqaual
                query.whereEqualTo("email", username);

                //go to table "Registers" and to col "password" doing the eqaual
                query.whereEqualTo("password", email);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> sList, ParseException e) {
                        //if we dont have a ParseException
                        if (e == null) {
                            // System.out.println("data:"+username+"\t"+email+"\t"+sList.size());
                            //User does not exist in parse
                            if (sList.size() == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                // Specify the list in the dialog using the array
                                builder.setTitle("שגיאה").setMessage("שם משתמש/סיסמא אינם חוקיים")
                                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

                                //create and show list dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                            //if user exist in parse
                            else {

                                emailid = username;

                                //Installation data in the local file for next time we will remember the user
                                editor = spUser.edit();
                                editor.putString("loginFlag", "true");
                                editor.putString("user", username);
                                editor.putString("email", email);
                                editor.commit();

                                //Updating the app's notifications to be TRUE
                                SvcReminder.isEye = true;
                                SvcReminder.isMog = true;
                                SvcReminder.isBreak = true;
                                SvcReminder.isLunch = true;
                                SvcReminder.isDinner = true;
                                SvcReminder.isBreak_0 = true;
                                SvcReminder.isLunch_0 = true;
                                SvcReminder.isDinner_0 = true;

                                //Passes from the login screen to the menu screen by intent
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();


                            }
                        } else {
                            //if we have a ParseException
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            // Specify the list in the dialog using the array
                            builder.setTitle("Error").setMessage(e.toString())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });

                            //create and show list dialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });

            }
        });
        //Connection between XML component to JAVA class
        btnForget = (Button) findViewById(R.id.btnForgot);

        btnForget.setOnClickListener(new View.OnClickListener() {
            //Passes from the LoginActivity screen to the forgets screen by intent
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, forgets.class));

            }
        });
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            //Passes from the LoginActivity screen to the forgets screen by intent
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, register.class));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


}