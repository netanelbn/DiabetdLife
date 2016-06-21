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

public class Tests_Moglobin extends ActionBarActivity {

    //variables Declaration
    EditText etxtLastDate, etxtComments, etxtNextDate;
    Button btnSave;
    String email;
    SharedPreferences.Editor editor;
    SharedPreferences spUser;
    String Mog_Lastdate, Mog_Nextdate, Mog_Comments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests__moglobin);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        Mog_Lastdate = spUser.getString("Mog_Lastdate", "");
        Mog_Nextdate = spUser.getString("Mog_Nextdate", "");
        Mog_Comments = spUser.getString("Mog_Comments", "");

        ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        email = spUser.getString("user", "");
        etxtLastDate = (EditText) findViewById(R.id.etxtDateM);

        SetDate setDate = new SetDate(etxtLastDate, Tests_Moglobin.this);


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


                    c.add(Calendar.MONTH, 3);
                    mydate = new Date(c.getTimeInMillis());
                    etxtNextDate.setText(sdformat.format(mydate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });


        etxtNextDate = (EditText) findViewById(R.id.etxtNextDateM);
        etxtComments = (EditText) findViewById(R.id.etxtCommentM);
        btnSave = (Button) findViewById(R.id.btnSaveM);
        etxtLastDate.setText(Mog_Lastdate);
        etxtNextDate.setText(Mog_Nextdate);
        etxtComments.setText(Mog_Comments);
        getReminderMoglobin();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject registers = new ParseObject("MoglobinExam");
                registers.put("email", email);
                registers.put("lastdate", etxtLastDate.getText().toString());
                registers.put("nextdate", etxtNextDate.getText().toString());
                registers.put("comments", etxtComments.getText().toString());
                editor = spUser.edit();
                editor.putString("Mog_Lastdate", etxtLastDate.getText().toString());
                editor.putString("Mog_Nextdate", etxtNextDate.getText().toString());
                editor.putString("Mog_Comments", etxtComments.getText().toString());
                editor.commit();

                registers.saveInBackground();
                AlertDialog.Builder builder = new AlertDialog.Builder(Tests_Moglobin.this);
                builder.setTitle("בדיקת A1C").setMessage("המידע נשמר בהצלחה!")
                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getReminderMoglobin();
                                finish();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void getReminderMoglobin() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("MoglobinExam");
        query.orderByDescending("updatedAt");
        query.whereEqualTo("email", email);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, com.parse.ParseException e) {
                if (scoreList.size() != 0) {
                    ParseObject obj = scoreList.get(0);
                    SvcReminder.moglobinExam = obj.getString("nextdate");
                    etxtLastDate.setText(obj.getString("lastdate"));
                    etxtNextDate.setText(obj.getString("nextdate"));
                    etxtComments.setText(obj.getString("comments"));

                } else {

                }
            }
        });
    }

}
