package com.parse.DiabetsApplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


//ActionBarActivity
public class Reminder extends AppCompatActivity {

    //Declaration of variables

    public static String title;
    public static String body;
    TextView txtTitle,txtBody;
    Button btnOK;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        txtTitle=(TextView)findViewById(R.id.txtTitle);
        txtTitle.setText(title);
        txtBody=(TextView)findViewById(R.id.txtBody);
        txtBody.setText(body);
        mp = MediaPlayer.create(this, R.raw.ring);
        mp.start();
        btnOK=(Button)findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mp.stop();
                }catch (Exception ae){}
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
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
