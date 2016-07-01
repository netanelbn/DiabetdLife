package com.parse.DiabetsApplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Reminder extends ActionBarActivity
{

    //variables Declaration

    public static String title;
    public static String body;
    TextView txtTitle, txtBody;
    Button btnOK;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Connects the XML file to java file
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText(title);
        txtBody = (TextView) findViewById(R.id.txtBody);
        txtBody.setText(body);
        //the np3 ringtone
        mp = MediaPlayer.create(this, R.raw.ring);
        //start the mp3 ringtone
        mp.start();
        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    //stop the mp3 ringtone
                    mp.stop();
                }
                catch (Exception ae)
                {
                }
                finish();
            }
        });
    }

}
