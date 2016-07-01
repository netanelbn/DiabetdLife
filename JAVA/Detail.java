package com.parse.DiabetsApplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//ActionBarActivity
public class Detail extends ActionBarActivity
{
    //variables Declaration
    EditText etxtDate;
    ImageView imgSearch;
    TextView txtResult;
    List<ParseObject> myscoreList;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Sharedpreferences is an effective and convenient mechanism for saving data.
        // The data are saved in pairs key / value.
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        //Connection between XML component to JAVA class
        email = spUser.getString("user", "");
        etxtDate = (EditText) findViewById(R.id.etxtDateSearch);
        //put a keyboars with a date when the user want to find something.
        SetDate setDate = new SetDate(etxtDate, this);
        txtResult = (TextView) findViewById(R.id.txtResult);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComment(etxtDate.getText().toString(), myscoreList);
            }
        });


        ParseQuery<ParseObject> query = ParseQuery.getQuery("History");
        //sorts the results in ascending order by the testtime.
        query.addAscendingOrder("testtime");
        //sorts the results in ascending order by the mealtime.

        query.addAscendingOrder("mealtime");
        //take the result only when the email equal to the user

        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> scoreList, ParseException e)
            {
                if (e == null)
                {
                    myscoreList = scoreList;

                }
                else
                {

                }
            }
        });
    }

    public void showComment(String text, List<ParseObject> scoreList)
    {
        String comment = "";
        ParseObject objH;
        int count = scoreList.size();

        for (int i = 0; i < count; i++)
        {

            objH = scoreList.get(i);
            Date date = objH.getDate("testtime");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = format.format(date);
            //if choozen date equals to "testtime" in "History" table

            if (formattedDate.equals(text))
            {

                int type = objH.getInt("mealtime");

                String current = "תאריך:" + formattedDate + "\n";

                if (type == 0)
                {
                    current += "בארוחת הבוקר:\n";

                }

                if (type == 1)
                {
                    current += "בארוחת הצהרים:\n";


                }
                if (type == 2) {
                    current += "בארוחת הערב:\n";


                }


                current += "בדיקת הסוכר שלך הייתה:" + objH.getInt("bloodsugar") + "\n";
                current += "אכלת בארוחה:" + objH.getString("information") + "\n";
                comment += current + "\n----------------------------------------\n";

            }

        }
        txtResult.setText(comment);
    }


}
