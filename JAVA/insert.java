package com.parse.DiabetsApplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.DiabetsApplication.R;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class insert extends ActionBarActivity {

    Spinner spnMeal;
    EditText etxtBloodSugar,etxtInfor;
    Button btnInsert;
    int timeofmeal=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        spnMeal=(Spinner)findViewById(R.id.spnMeal);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mealtime, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnMeal.setAdapter(adapter);
        spnMeal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeofmeal=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etxtBloodSugar=(EditText)findViewById(R.id.etxtBloodSugar);
        etxtInfor=(EditText)findViewById(R.id.etxtInfor);
        btnInsert=(Button)findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject registers = new ParseObject("History");
                registers.put("email", LoginActivity.emailid);
                registers.put("testtime", new Date());
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String formattedDate = format.format(new Date());
                int bloodsugar=Integer.parseInt(etxtBloodSugar.getText().toString());
                registers.put("bloodsugar",bloodsugar );
                registers.put("mealtime", timeofmeal);
                registers.put("mysort",formattedDate+timeofmeal);
                registers.put("information", etxtInfor.getText().toString());
                registers.saveInBackground();
                AlertDialog.Builder builder = new AlertDialog.Builder(insert.this);
                // Specify the list in the dialog using the array
                builder.setTitle("insert").setMessage("save data is successfull")
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        int id = item.getItemId();

      
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
