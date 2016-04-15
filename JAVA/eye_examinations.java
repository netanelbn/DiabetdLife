package com.parse.DiabetsApplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.DiabetsApplication.R;
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

    EditText etxtLastDate,etxtComments,etxtNextDate;
    Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_examinations);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        etxtLastDate=(EditText)findViewById(R.id.etxtDateEye);
        SetDate setDate=new SetDate(etxtLastDate,eye_examinations.this);
        etxtLastDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                Date mydate=new Date();
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
        etxtNextDate=(EditText)findViewById(R.id.etxtNextDate);
        etxtComments=(EditText)findViewById(R.id.etxtCommentEye);
        btnSave=(Button)findViewById(R.id.btnSaveEye);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject registers = new ParseObject("EyeExam");
                registers.put("email", LoginActivity.emailid);
                registers.put("lastdate", etxtLastDate.getText().toString());
                registers.put("nextdate", etxtNextDate.getText().toString());
                registers.put("comments", etxtComments.getText().toString());
                registers.saveInBackground();
                AlertDialog.Builder builder = new AlertDialog.Builder(eye_examinations.this);
                // Specify the list in the dialog using the array
                builder.setTitle("Eye Examinations").setMessage("save data is successfull")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
    public void getReminderEye()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("EyeExam");
        query.addDescendingOrder("nextdate");
        query.whereEqualTo("email", LoginActivity.emailid);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, com.parse.ParseException e) {
                if (e == null) {
                    ParseObject obj=scoreList.get(0);
                    SvcReminder.eyeExam=obj.getString("nextdate");
                } else {

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eye_examinations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
