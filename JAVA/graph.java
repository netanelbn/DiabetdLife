package com.parse.DiabetsApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.YPositionMetric;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.ValueMarker;
import com.androidplot.xy.XValueMarker;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YValueMarker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class graph extends Activity implements OnTouchListener {

    private XYPlot mySimpleXYPlot;
    private Button btnCapture, btnOption;
    private PointF minXY;
    private PointF maxXY;
    ProgressDialog pDialog;
    private boolean iscapture = false;
    ImageView imgback;
    final String[] itemList = {"שליחת כל הבדיקות לאימייל", "חיפוש לפי תאריך מסויים"};
    Number[] yValues;//[{80,80,90,70,100,120,12]0}
    Number[] xValues;// xValues=new Number[]{0,0.3,0.6,0.9,1.3,1.6,2};
    public String comment = "";
    String imgurl = "empty";
    EditText txtMyDate;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //Sharedpreferences is an effective and convenient mechanism for saving data.
        // The data are saved in pairs key / value.
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        email = spUser.getString("user", "");

        //Connection between XML component to JAVA class
        txtMyDate = (EditText) findViewById(R.id.etxtMyDate);
        SetDate setDate = new SetDate(txtMyDate, this);
        imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        //get from PARSE the table history
        ParseQuery<ParseObject> query = ParseQuery.getQuery("History");
        // Sorts the results in descending order by the score field
        query.addAscendingOrder("mysort");
        query.whereEqualTo("email", LoginActivity.emailid);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    if (scoreList.size() != 0) {
                        uploadData(scoreList);
                    }

                } else {

                }
            }
        });
        btnOption = (Button) findViewById(R.id.btnOption);
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(graph.this);
                builder.setTitle("אפשרויות");
                builder.setItems(itemList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0)//the user choosed send to email
                        {
                            if (!iscapture) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(graph.this);
                                // Specify the list in the dialog using the array
                                builder.setTitle("שליחת אימייל").setMessage("לפני שליחת בדיקות לאימייל יש לחתוך את התמונה! ")
                                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

                                //create and show list dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                pDialog = new ProgressDialog(graph.this);
                                pDialog.setMessage("שולח בדיקות לאימייל אנא המתן...");
                                //"loading amount" is not measured.
                                pDialog.setIndeterminate(false);
                                //Sets whether this dialog is cancelable with the BACK key
                                pDialog.setCancelable(true);
                                pDialog.show();
                                Thread th = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //util the pic go into the server Parse
                                        while (imgurl.equals("empty")) {
                                            try {
                                                Thread.sleep(5000);

                                            } catch (Exception ae) {
                                            }
                                        }
                                        comment += "\n" + " קישור לתמונת הגרף:" + imgurl;
                                        new SendToEmail().execute(comment);
                                    }
                                });
                                //to new Runnable() in asyncrone Task
                                th.start();

                            }
                        } else {
                            //if the user dont send a mail we start a new activity detail(i=1)
                            startActivity(new Intent(graph.this, Detail.class));
                            finish();

                        }
                    }
                });
                builder.show();
            }
        });


        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to picture table when the email equall to email user
                ParseObject gameScore = new ParseObject("Pictures");
                gameScore.put("email", LoginActivity.emailid);
                //Enables or disables the Cache cache.
                //When the drawing cache is enabled, the next call to getDrawingCache() or buildDrawingCache()
                // will draw the view in a bitmap.

                //we open the cache to the pic
                mySimpleXYPlot.setDrawingCacheEnabled(true);

                FileOutputStream fos = null;
                try {
                    //The path of images saved to the user's device
                    //if the second argument is true, then bytes will be written to the end of the file rather than the beginning
                    fos = new FileOutputStream("/sdcard/DCIM/img.jpg", false);
                    //we have to compress the picture before saving here in the parse server
                    mySimpleXYPlot.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    iscapture = true;
                    //we close the cache after compress the pic
                    mySimpleXYPlot.setDrawingCacheEnabled(false);
                    //after compress the file we take the "ready" file from the same STREAM "/sdcard/DCIM/img.jpg"

                    File file = new File("/sdcard/DCIM/img.jpg");
                    final ParseFile pfile = new ParseFile(file);
                    //save the data in PARSE
                    gameScore.put("image", pfile);

                    gameScore.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            imgurl = pfile.getUrl().toString();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }

    //method that uplad all the Data
    public void uploadData(List<ParseObject> scoreList) {
        //Get a reference to the XYplot defined in the XML.
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        if (scoreList.size() <= 0) return;
        ParseObject objH = scoreList.get(0);
        //in the table history we have a clmoun in the name"testtime"
        Date date = objH.getDate("testtime");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM");
        String formattedDate = format.format(date);
        int count = scoreList.size();

        //1 cell to the title and 2 cell because the for loop count<i

        yValues = new Number[count + 2];//new Number[{80,80,90,70,100,120,12]0};
        xValues = new Number[count + 2];//new Number[]{0,0.3,0.6,0.9,1.3,1.6,2};
        xLabels = new String[count + 2];//{"17/11","18/11","19/11","20/11","21/11","22/11"}
        comment = "";

        //intilize all the param graph
        //  yValues[0] = objH.getInt("bloodsugar");
        xValues[0] = 0;
        xLabels[0] = formattedDate;

        // Create a Calendar object
        Calendar c = Calendar.getInstance();

        double nextvalue = 0.0;
        double curvalue = 0.0;
        //postioin to all meals titels(600 in the xyplot)
        YPositionMetric point = new YPositionMetric(100, YLayoutStyle.ABSOLUTE_FROM_TOP);
        XValueMarker marker;
        //often the scorelist.size = 3 per 1 day.
        for (int i = 0; i < count; i++) {
            objH = scoreList.get(i);
            yValues[i + 1] = objH.getInt("bloodsugar");
            int type = objH.getInt("mealtime");
            String current = "Date:" + objH.getDate("testtime").toString() + "\n";
            //if we are in the breakfast:
            if (type == 0) {
                curvalue = nextvalue + 0.3;
                current += "בארוחת הבוקר\n";
                marker = new XValueMarker(curvalue, "בוקר");
                marker.setTextPosition(point);
                marker.setTextOrientation(ValueMarker.TextOrientation.HORIZONTAL);
                marker.getLinePaint().setColor(Color.BLUE);
                marker.getTextPaint().setColor(Color.BLUE);
                mySimpleXYPlot.addMarker(marker);
            }
            //if we are in the lunch:
            if (type == 1) {
                curvalue = nextvalue + 0.6;
                current += "בארוחת הצהרים\n";
                marker = new XValueMarker(curvalue, "צהרים");
                marker.setTextPosition(point);
                marker.setTextOrientation(ValueMarker.TextOrientation.HORIZONTAL);
                marker.getLinePaint().setColor(Color.BLUE);
                marker.getTextPaint().setColor(Color.BLUE);
                mySimpleXYPlot.addMarker(marker);
            }
            //if we are in the Dinner:
            if (type == 2) {
                curvalue = nextvalue + 0.9;
                current += "בארוחת ערב\n";
                //in the night we preper the array to the next day
                nextvalue = nextvalue + 1;
                marker = new XValueMarker(curvalue, "ערב");
                marker.setTextPosition(point);
                marker.setTextOrientation(ValueMarker.TextOrientation.HORIZONTAL);
                marker.getLinePaint().setColor(Color.BLUE);
                marker.getTextPaint().setColor(Color.BLUE);
                mySimpleXYPlot.addMarker(marker);
            }


            current += "רמת הסוכר שלך הייתה:" + objH.getInt("bloodsugar") + "\n";
            current += "אכלת:" + objH.getString("information") + "\n";
            xValues[i + 1] = curvalue;
            c.setTime(date);
            c.add(Calendar.DATE, 1);
            date = new Date(c.getTimeInMillis());
            formattedDate = format.format(date);
            xLabels[i + 1] = formattedDate;

            comment += current + "\n----------------------------------------\n";
        }


        //make a Market for Y...red color its the difault
        YValueMarker ymarket = new YValueMarker(100, "טוב");
        ymarket.getLinePaint().setColor(Color.BLUE);
        ymarket.getTextPaint().setColor(Color.BLUE);
        mySimpleXYPlot.addMarker(ymarket);
        ymarket = new YValueMarker(150, "גבוה");
        mySimpleXYPlot.addMarker(ymarket);

        ymarket = new YValueMarker(70, "נמוך");
        ymarket.getLinePaint().setColor(Color.YELLOW);
        ymarket.getTextPaint().setColor(Color.YELLOW);
        mySimpleXYPlot.addMarker(ymarket);
        // Create a couple arrays of y-values to plot:
        mySimpleXYPlot.setRangeBoundaries(0, 710, BoundaryMode.FIXED);

        //3 titels "Blood sugar" in the graph
        mySimpleXYPlot.setDomainLabel("Blood sugar");
        mySimpleXYPlot.setRangeLabel("Blood sugar");
        mySimpleXYPlot.setTitle("Blood sugar");
        mySimpleXYPlot.setOnTouchListener(this);
        mySimpleXYPlot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat());
        //Turn the above arrays into XYSeries:
        XYSeries series = new SimpleXYSeries(Arrays.asList(xValues), Arrays.asList(yValues), "תאריך");
        // Add xValues and yValues to the xyplot:
        // Color.rgb (int red,int green,int blue)
        //new LineAndPointFormatter(lines color,points color,null,point number)
        mySimpleXYPlot.addSeries(series, new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100), null, new PointLabelFormatter(Color.WHITE)));
        //step for the x-date
        mySimpleXYPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        mySimpleXYPlot.setDomainStepValue(1);

        //step for y-bloodsugar
        mySimpleXYPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        mySimpleXYPlot.setRangeStepValue(50);
        //update the graph
        mySimpleXYPlot.redraw();
        mySimpleXYPlot.calculateMinMaxVals();
        minXY = new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(), mySimpleXYPlot.getCalculatedMinY().floatValue());
        maxXY = new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(), mySimpleXYPlot.getCalculatedMaxY().floatValue());
    }


    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float distBetweenFingers;
    boolean stopThread = false;


    //View arg0: The view the touch event has been dispatched to.
    //MotionEvent event: The MotionEvent object containing full information about the event.
    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //When the user places his first finger on the View..
            case MotionEvent.ACTION_DOWN: // Start gesture
                //in case ACTION_DOWN we get the Location of X and Y

                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                stopThread = true;
                break;


            // ACTION UP - When He picks up the finger from the View (and has only one finger on View)
            case MotionEvent.ACTION_UP:

                //ACTION_POINTER_UP - when he left the finger from the screen and just 1 finger toche the screen.
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;

            // When the user puts his anther finger (or third or more) on the screen.
            case MotionEvent.ACTION_POINTER_DOWN: //second finger
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ONE_FINGER_DRAG) {
                    PointF oldFirstFinger = firstFinger;
                    firstFinger = new PointF(event.getX(), event.getY());
                    scroll(oldFirstFinger.x - firstFinger.x);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
                    mySimpleXYPlot.redraw();

                } else if (mode == TWO_FINGERS_DRAG) {
                    float oldDist = distBetweenFingers;
                    distBetweenFingers = spacing(event);
                    zoom(oldDist / distBetweenFingers);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
                    mySimpleXYPlot.redraw();
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = maxXY.x - minXY.x;
        float domainMidPoint = maxXY.x - domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;

        minXY.x = domainMidPoint - offset;
        maxXY.x = domainMidPoint + offset;

        //Returns the most negative of the two arguments.
        minXY.x = Math.min(minXY.x, 31);
        //Returns the most positive of the two arguments.
        maxXY.x = Math.max(maxXY.x, 0);

        clampToDomainBounds(domainSpan);
    }

    private void scroll(float pan) {
        // distance
        float domainSpan = maxXY.x - minXY.x;
        float step = domainSpan / mySimpleXYPlot.getWidth();
        //// distance between finger * % of increment
        float offset = pan * step;
        minXY.x = minXY.x + offset;
        maxXY.x = maxXY.x + offset;
        clampToDomainBounds(domainSpan);
    }

    private void clampToDomainBounds(float domainSpan) {
        float leftBoundary = 0;
        float rightBoundary = 31;
        // enforce left scroll boundary:
        if (minXY.x < leftBoundary) {
            minXY.x = leftBoundary;
            maxXY.x = leftBoundary + domainSpan;
        } else if (maxXY.x > 31) {
            maxXY.x = rightBoundary;
            minXY.x = rightBoundary - domainSpan;
        }
    }

    //Checking the distance between the two fingers
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    String[] xLabels;

    class GraphXLabelFormat extends Format {

        @Override
        public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {

            int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
            Log.d("test", parsedInt + " " + arg1 + " " + arg2);
            String labelString = xLabels[parsedInt];
            arg1.append(labelString);
            return arg1;
        }

        @Override
        public Object parseObject(String arg0, ParsePosition arg1) {

            return Arrays.asList(xLabels).indexOf(arg0);
        }
    }


    //  The parameters will be inserted to doOnBackground String.
    //  The parameters are passed to onProgressUpdate be String.
    //  The parameters to be passed to onPostExecute type String.
    class SendToEmail extends AsyncTask<String, String, String> {

        //This method is running in the UI thread.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //This method is part of the new thread running in--.
        //This is the only method must implement, and the only one who ran in the UI thread.
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag

                    try {
                        GMailSender sender = new GMailSender(Constrants.username, Constrants.password);
                        sender.sendMail("נתוני בדיקות הסוכר שלך!", comment, Constrants.username, LoginActivity.emailid);
                    } catch (Exception exp) {
                        Log.e("SendMail", exp.getMessage(), exp);
                    }
                }
            });

            return null;
        }


        //This method is activated after that doInBackground finished his work.
// Receives from doInBackground the results and can process them.
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            // dismiss the dialog once got all details
            AlertDialog.Builder builder = new AlertDialog.Builder(graph.this);
            // Specify the list in the dialog using the array
            builder.setTitle("שליחת נתונים לאימייל").setMessage("שליחת נתוני הבדיקות לאימייל התבצעה בהצלחה!")
                    .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            //create and show list dialog
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

}