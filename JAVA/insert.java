package com.parse.DiabetsApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class insert extends ActionBarActivity {
    //variables Declaration
    Spinner spnMeal;
    EditText etxtBloodSugar, etxtInfor;
    Button btnInsert;
    int timeofmeal = 0;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

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

        email = spUser.getString("user", "");
        spnMeal = (Spinner) findViewById(R.id.spnMeal);
        //the adapter connect the data that we have and the view from the xml
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mealtime, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnMeal.setAdapter(adapter);
        spnMeal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeofmeal = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Connection between XML component to JAVA class
        etxtBloodSugar = (EditText) findViewById(R.id.etxtBloodSugar);
        etxtInfor = (EditText) findViewById(R.id.etxtInfor);
        btnInsert = (Button) findViewById(R.id.btnInsert);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to "History" table in parse
                ParseObject registers = new ParseObject("History");
                //go to "History" table in parse and to "email" col
                registers.put("email", email);
                //put new dare into parse server
                registers.put("testtime", new Date());

                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = format.format(new Date());
                int bloodsugar = Integer.parseInt(etxtBloodSugar.getText().toString());
                registers.put("bloodsugar", bloodsugar);

                //timeofmeal recive "0" or "1" or "2"
                registers.put("mealtime", timeofmeal);

                //for example in parse its be like this:201605232 that mean:2016-05-23 in the dinner(2)
                registers.put("mysort", formattedDate + timeofmeal);

                registers.put("information", etxtInfor.getText().toString());
                registers.saveInBackground();

                AlertDialog.Builder builder = new AlertDialog.Builder(insert.this);
                // Specify the list in the dialog using the array
                builder.setTitle("בדיקת סוכר").setMessage("המידע נשמר בהצלחה!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });

                //create and show list dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


}

