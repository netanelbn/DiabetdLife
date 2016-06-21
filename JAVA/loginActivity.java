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


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setContentView(R.layout.layout_login);
        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtEmail = (EditText) findViewById(R.id.etxtEmail);
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        username = spUser.getString("user", "");
        email = spUser.getString("email", "");
        loginFlag = spUser.getString("loginFlag", "");

        if (loginFlag.equals("true")) {
        
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        etxtName.setText(username);
        etxtEmail.setText(email);


        chkRemember = (CheckBox) findViewById(R.id.chkRemember);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = etxtName.getText().toString();
                email = etxtEmail.getText().toString();
                if (chkRemember.isChecked()) {                    editor = spUser.edit();
                    editor.putString("user", username);
                    editor.putString("email", email);
                    editor.commit();
                }
          
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");


                query.whereEqualTo("email", username);


                query.whereEqualTo("password", email);

                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> sList, ParseException e) {
                        if (e == null) {


                            if (sList.size() == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                                builder.setTitle("שגיאה").setMessage("שם משתמש/סיסמא אינם חוקיים")
                                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }


                            else {

                                emailid = username;

                                editor = spUser.edit();
                                editor.putString("loginFlag", "true");
                                editor.putString("user", username);
                                editor.putString("email", email);
                                editor.commit();

                                SvcReminder.isEye = true;
                                SvcReminder.isMog = true;
                                SvcReminder.isBreak = true;
                                SvcReminder.isLunch = true;
                                SvcReminder.isDinner = true;
                                SvcReminder.isBreak_0 = true;
                                SvcReminder.isLunch_0 = true;
                                SvcReminder.isDinner_0 = true;

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();


                            }
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                            builder.setTitle("Error").setMessage(e.toString())
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });

            }
        });

        btnForget = (Button) findViewById(R.id.btnForgot);

        btnForget.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, forgets.class));

            }
        });
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
   
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