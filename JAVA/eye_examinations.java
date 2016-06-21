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

        spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

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

        SetDate setDate = new SetDate(etxtLastDate, eye_examinations.this);

        etxtLastDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }


            @Override
            public void afterTextChanged(Editable editable) {
                String myFormat = "yyyy-MM-dd"; 
                SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                Date mydate = new Date();
                try {
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdformat.parse(etxtLastDate.getText().toString()));

                    c.add(Calendar.MONTH, 6);
                    mydate = new Date(c.getTimeInMillis());
                    etxtNextDate.setText(sdformat.format(mydate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        etxtNextDate = (EditText) findViewById(R.id.etxtNextDate);
        etxtComments = (EditText) findViewById(R.id.etxtCommentEye);
        btnSave = (Button) findViewById(R.id.btnSaveEye);

        etxtLastDate.setText(Eye_Lastdate);
        etxtNextDate.setText(Eye_Nextdate);
        etxtComments.setText(Eye_Comments);

        getReminderEye();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                ParseObject registers = new ParseObject("EyeExam");
                registers.put("email", email);
                registers.put("lastdate", etxtLastDate.getText().toString());
                registers.put("nextdate", etxtNextDate.getText().toString());
                registers.put("comments", etxtComments.getText().toString());

                
                editor = spUser.edit();
                editor.putString("Eye_Lastdate", etxtLastDate.getText().toString());
                editor.putString("Eye_Nextdate", etxtNextDate.getText().toString());
                editor.putString("Eye_Comments", etxtComments.getText().toString());

                editor.commit();


                registers.saveInBackground();

                AlertDialog.Builder builder = new AlertDialog.Builder(eye_examinations.this);
                builder.setTitle("בדיקת עיניים").setMessage("המידע נשמר בהצלחה!")
                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getReminderEye();
                                finish();
                            }
                        });

            
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }


    public void getReminderEye() {
 
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EyeExam");
        query.orderByDescending("updatedAt");
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, com.parse.ParseException e) {
                if (scoreList.size() != 0) {
                    ParseObject obj = scoreList.get(0);
                    SvcReminder.eyeExam = obj.getString("nextdate");
                    etxtLastDate.setText(obj.getString("lastdate"));
                    etxtNextDate.setText(obj.getString("nextdate"));
                    etxtComments.setText(obj.getString("comments"));

                } else {

                }
            }
        });
    }


}
