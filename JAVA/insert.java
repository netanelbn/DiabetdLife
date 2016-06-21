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
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mealtime, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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

        etxtBloodSugar = (EditText) findViewById(R.id.etxtBloodSugar);
        etxtInfor = (EditText) findViewById(R.id.etxtInfor);
        btnInsert = (Button) findViewById(R.id.btnInsert);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    
                ParseObject registers = new ParseObject("History");
                registers.put("email", email);
                registers.put("testtime", new Date());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = format.format(new Date());
                int bloodsugar = Integer.parseInt(etxtBloodSugar.getText().toString());
                registers.put("bloodsugar", bloodsugar);
                registers.put("mealtime", timeofmeal);
                registers.put("mysort", formattedDate + timeofmeal);
                registers.put("information", etxtInfor.getText().toString());
                registers.saveInBackground();
                AlertDialog.Builder builder = new AlertDialog.Builder(insert.this);
                builder.setTitle("בדיקת סוכר").setMessage("המידע נשמר בהצלחה!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });


                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


}

