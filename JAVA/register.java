package com.parse.DiabetsApplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;

import android.widget.Toast;
import android.text.TextUtils;

import com.parse.ParseACL;


public class register extends ActionBarActivity {

    //Declaration of variables

    EditText etxtName, etxtEmail, etxtPassword, etxtAddress, etxtBreakTime, etxtLunch, etxtDinner;
    Button btnRegister;
    TextView txtLink;

    /**
     * This integer will uniquely define the dialog to be used for displaying time picker.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ParseAnalytics.trackAppOpenedInBackground(getIntent());


        etxtName = (EditText) findViewById(R.id.etxtName);
        etxtEmail = (EditText) findViewById(R.id.etxtEmail);
        etxtPassword = (EditText) findViewById(R.id.etxtPassword);
        etxtAddress = (EditText) findViewById(R.id.etxtAddress);
        etxtBreakTime = (EditText) findViewById(R.id.etxtBreakTime);
        SetTime setBreakfast = new SetTime(etxtBreakTime, register.this);
        etxtLunch = (EditText) findViewById(R.id.etxtLunch);
        SetTime setLunch = new SetTime(etxtLunch, register.this);
        etxtDinner = (EditText) findViewById(R.id.etxtDinner);
        SetTime setDinner = new SetTime(etxtDinner, register.this);
        btnRegister = (Button) findViewById(R.id.btnRRegister);
        btnRegister.setOnClickListener(btnRegisterClick);
        txtLink = (TextView) findViewById(R.id.link_to_login);

        txtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

    }

    android.view.View.OnClickListener btnRegisterClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String username = etxtName.getText().toString();
            String email = etxtEmail.getText().toString();
            String password = etxtPassword.getText().toString();
            String address = etxtAddress.getText().toString();
            String breakfasttime = etxtBreakTime.getText().toString();
            String lunchtime = etxtLunch.getText().toString();
            String dinnertime = etxtDinner.getText().toString();

            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(address) &&
                    !TextUtils.isEmpty(breakfasttime) && !TextUtils.isEmpty(lunchtime) && !TextUtils.isEmpty(dinnertime)) {
                ParseObject registers = new ParseObject("Registers");

                AlertDialog.Builder builder = new AlertDialog.Builder(register.this);

                registers.put("username", etxtName.getText().toString());
                registers.put("email", etxtEmail.getText().toString());
                registers.put("password", etxtPassword.getText().toString());
                registers.put("address", etxtAddress.getText().toString());
                registers.put("breakfasttime", etxtBreakTime.getText().toString());
                registers.put("lunchtime", etxtLunch.getText().toString());
                registers.put("dinnertime", etxtDinner.getText().toString());

                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);
                registers.setACL(acl);

                registers.saveInBackground();

                builder.setTitle("ברוכים הבאים!").setMessage("נרשמת בהצלחה")
                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                Toast.makeText(register.this, "אנא מלא את כל השדות...", Toast.LENGTH_SHORT).show();
            }

        }

    };


}