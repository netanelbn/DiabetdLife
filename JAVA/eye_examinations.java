package com.parse.DiabetsApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class eye_examinations extends ActionBarActivity {

    //variables Declaration
    EditText etxtLastDate, etxtComments, etxtNextDate;
    Button btnSave;
    String email;
    SharedPreferences.Editor editor;
    SharedPreferences spUser;
    String Eye_Lastdate, Eye_Nextdate, Eye_Comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_examinations);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        //Creates a file system called USER and there keeping the user's data
        spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        //takes the data  from PARSE and places them in the background app
        // The user's information go right into the app. In case its a user: data fields will be empty
        Eye_Lastdate = spUser.getString("Eye_Lastdate", "");
        Eye_Nextdate = spUser.getString("Eye_Nextdate", "");
        Eye_Comments = spUser.getString("Eye_Comments", "");

        ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        email = spUser.getString("user", "");
        etxtLastDate = (EditText) findViewById(R.id.etxtDateEye);

        //Opens a window of event dates and press the button last date
        SetDate setDate = new SetDate(etxtLastDate, eye_examinations.this);

        //We listen to that button because the system needs to know if we've made a change the lastdate
        etxtLastDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            //This method is called to notify you that, somewhere within editable, the text has been changed.
            @Override
            public void afterTextChanged(Editable editable) {
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                Date mydate = new Date();
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdformat.parse(etxtLastDate.getText().toString()));

                    //Adds another six months before the last date to the next examination test
                    c.add(Calendar.MONTH, 6);
                    mydate = new Date(c.getTimeInMillis());
                    etxtNextDate.setText(sdformat.format(mydate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        //Connects the XML file to a file with Java
        etxtNextDate = (EditText) findViewById(R.id.etxtNextDate);
        etxtComments = (EditText) findViewById(R.id.etxtCommentEye);
        btnSave = (Button) findViewById(R.id.btnSaveEye);

        //Receives the user information from thr "USER" file from SharedPreferences
        etxtLastDate.setText(Eye_Lastdate);
        etxtNextDate.setText(Eye_Nextdate);
        etxtComments.setText(Eye_Comments);

        getReminderEye();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Puts the user's data into the server PARSE in the "EyeExam" table:
                ParseObject registers = new ParseObject("EyeExam");
                registers.put("email", email);
                registers.put("lastdate", etxtLastDate.getText().toString());
                registers.put("nextdate", etxtNextDate.getText().toString());
                registers.put("comments", etxtComments.getText().toString());

                //Puts the user's data into the "USER" fille
                editor = spUser.edit();
                editor.putString("Eye_Lastdate", etxtLastDate.getText().toString());
                editor.putString("Eye_Nextdate", etxtNextDate.getText().toString());
                editor.putString("Eye_Comments", etxtComments.getText().toString());

                //Updating the data in file
                editor.commit();


                registers.saveInBackground();

                AlertDialog.Builder builder = new AlertDialog.Builder(eye_examinations.this);
                // Specify the list in the dialog using the array
                builder.setTitle("בדיקת עיניים").setMessage("המידע נשמר בהצלחה!")
                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getReminderEye();
                                finish();
                            }
                        });

                //create and show list dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    //Method is responsible for eye Reminder
    public void getReminderEye() {
        //go to table "EyeExam" in Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EyeExam");
        // Sorts the column by date because for 1 user we can have some checks...we want the last check.
        query.orderByDescending("updatedAt");
        //go to col "email" in  "EyeExam" table in Parse
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, com.parse.ParseException e) {
                if (scoreList.size() != 0) {
                    ParseObject obj = scoreList.get(0);

                    //Sends the next examination date to  SvcReminder class
                    SvcReminder.eyeExam = obj.getString("nextdate");

                    //Taking data from the server and places them in the background
                    etxtLastDate.setText(obj.getString("lastdate"));
                    etxtNextDate.setText(obj.getString("nextdate"));
                    etxtComments.setText(obj.getString("comments"));

                } else {

                }
            }
        });
    }


}
