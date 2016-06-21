package com.parse.DiabetsApplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.Random;

public class forgets extends ActionBarActivity {
    //variables Declaration
    EditText etxtEmail;
    Button btnSendEmail;
    String newPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgets);
        etxtEmail = (EditText) findViewById(R.id.etxtEmailF);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);



        ImageView imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });


        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Registers");
                query.whereEqualTo("email", etxtEmail.getText().toString());
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scoreList, ParseException e) {
                        if (e == null) {
                            Random rn = new Random();

                            if (scoreList.size() > 0) {

                                ParseObject object = scoreList.get(0);
                                newPass = object.getString("password");
                                new newPassWord().execute(newPass);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(forgets.this);
                       
                                builder.setTitle("שגיאה!").setMessage("האימייל לא רשום במערכת")
                                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });


                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } else {

                        }
                    }
                });
            }
        });
    }





    class newPassWord extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected String doInBackground(String... params) {


            runOnUiThread(new Runnable() {
                public void run() {
          
                    try {
                
                        String comment = "שלום משתמש יקר!" + "\n" + "הסיסמא שלך היא:" + newPass + "\n" + "אנא נסה להתחבר שוב בבקשה!";
                        GMailSender sender = new GMailSender(Constrants.username, Constrants.password);
                        sender.sendMail("אפליקציית סכרת-שחזור סיסמה", comment, Constrants.username, etxtEmail.getText().toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return "";
        }



        protected void onPostExecute(String file_url) {
 
            AlertDialog.Builder builder = new AlertDialog.Builder(forgets.this);
           
            builder.setTitle("שחזור סיסמה").setMessage("הסיסמה שלך נשלחה בהצלחה לאימייל!")
                    .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


}
