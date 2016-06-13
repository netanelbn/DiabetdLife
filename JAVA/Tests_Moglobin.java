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

public class Tests_Moglobin extends ActionBarActivity
{

    //variables Declaration
    EditText etxtLastDate,etxtComments,etxtNextDate;
    Button btnSave;
    String email;
    SharedPreferences.Editor editor;
    SharedPreferences spUser;
    String Mog_Lastdate,Mog_Nextdate,Mog_Comments;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests__moglobin);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        //Creates a file system called USER and there keeping the user's data
        spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        //takes the data  from PARSE and places them in the background app
        // The user's information go right into the app. In case its a user: data fields will be empty
        Mog_Lastdate = spUser.getString("Mog_Lastdate", "");
        Mog_Nextdate = spUser.getString("Mog_Nextdate", "");
        Mog_Comments = spUser.getString("Mog_Comments","");

        ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
                finish();
            }
        });

        email=spUser.getString("user","");
        etxtLastDate=(EditText)findViewById(R.id.etxtDateM);

        //Opens a window of event dates and press the button last date
        SetDate setDate=new SetDate(etxtLastDate,Tests_Moglobin.this);


        //We listen to that button because the system needs to know if we've made a change the lastdate
        etxtLastDate.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }


            //This method is called to notify you that, somewhere within editable, the text has been changed.
            @Override
            public void afterTextChanged(Editable editable)
            {
                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
                Date mydate=new Date();
                try
                {
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdformat.parse(etxtLastDate.getText().toString()));

                    //Adds another six months before the last date to the next examination test
                    c.add(Calendar.MONTH, 3);
                    mydate = new Date(c.getTimeInMillis());
                    etxtNextDate.setText(sdformat.format(mydate));
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }


            }
        });

        //Connects the XML file to a file with Java
        etxtNextDate=(EditText)findViewById(R.id.etxtNextDateM);
        etxtComments=(EditText)findViewById(R.id.etxtCommentM);
        btnSave=(Button)findViewById(R.id.btnSaveM);

        //Receives the user information from thr "USER" file from SharedPreferences
        etxtLastDate.setText(Mog_Lastdate);
        etxtNextDate.setText(Mog_Nextdate);
        etxtComments.setText(Mog_Comments);

        //Method is responsible for Moglobin Reminder
        getReminderMoglobin();

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                //Puts the user's data into the server PARSE in the "MoglobinExam" table:
                ParseObject registers = new ParseObject("MoglobinExam");
                registers.put("email", email);
                registers.put("lastdate", etxtLastDate.getText().toString());
                registers.put("nextdate", etxtNextDate.getText().toString());
                registers.put("comments", etxtComments.getText().toString());

                //Puts the user's data into the "USER" fille
                editor = spUser.edit();
                editor.putString("Mog_Lastdate", etxtLastDate.getText().toString());
                editor.putString("Mog_Nextdate", etxtNextDate.getText().toString());
                editor.putString("Mog_Comments", etxtComments.getText().toString());
                editor.commit();

                registers.saveInBackground();
                AlertDialog.Builder builder = new AlertDialog.Builder(Tests_Moglobin.this);
                // Specify the list in the dialog using the array
                builder.setTitle("בדיקת A1C").setMessage("המידע נשמר בהצלחה!")
                        .setPositiveButton("אישור", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                getReminderMoglobin();
                                finish();
                            }
                        });

                //create and show list dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void getReminderMoglobin()
    {

        //go to table "MoglobinExam" in Parse
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MoglobinExam");

        // Sorts the column by date because for 1 user we can have some checks...we want the last check.
        query.orderByDescending("updatedAt");

        //go to col "email" in  "EyeExam" table in Parse
        query.whereEqualTo("email", email);

        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> scoreList, com.parse.ParseException e)
            {
                if (scoreList.size() != 0)
                {
                    ParseObject obj=scoreList.get(0);

                    //Sends the next examination date to  SvcReminder class
                    SvcReminder.moglobinExam=obj.getString("nextdate");

                    //Taking data from the server and places them in the background
                    etxtLastDate.setText(obj.getString("lastdate"));
                    etxtNextDate.setText(obj.getString("nextdate"));
                    etxtComments.setText(obj.getString("comments"));

                }
                else
                {

                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tests__moglobin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
