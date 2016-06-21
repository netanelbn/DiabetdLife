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
    Number[] yValues;
    Number[] xValues;
    public String comment = "";
    String imgurl = "empty";
    EditText txtMyDate;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);
        email = spUser.getString("user", "");
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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("History");
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
                        if (i == 0)
                        {
                            if (!iscapture) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(graph.this);        
                                builder.setTitle("שליחת אימייל").setMessage("לפני שליחת בדיקות לאימייל יש לחתוך את התמונה! ")
                                        .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

         
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                pDialog = new ProgressDialog(graph.this);
                                pDialog.setMessage("שולח בדיקות לאימייל אנא המתן...");
                                pDialog.setIndeterminate(false);
                                pDialog.setCancelable(true);
                                pDialog.show();
                                Thread th = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
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

                                th.start();

                            }
                        } else {
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

                ParseObject gameScore = new ParseObject("Pictures");
                gameScore.put("email", LoginActivity.emailid);
                mySimpleXYPlot.setDrawingCacheEnabled(true);

                FileOutputStream fos = null;
                try {
                   fos = new FileOutputStream("/sdcard/DCIM/img.jpg", false);
                    mySimpleXYPlot.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                    iscapture = true;
                    mySimpleXYPlot.setDrawingCacheEnabled(false);
                    File file = new File("/sdcard/DCIM/img.jpg");
                    final ParseFile pfile = new ParseFile(file);

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


    public void uploadData(List<ParseObject> scoreList) {
.
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        if (scoreList.size() <= 0) return;
        ParseObject objH = scoreList.get(0);

        Date date = objH.getDate("testtime");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM");
        String formattedDate = format.format(date);
        int count = scoreList.size();

 

        yValues = new Number[count + 2];
        xValues = new Number[count + 2];
        xLabels = new String[count + 2];
        comment = "";


        xValues[0] = 0;
        xLabels[0] = formattedDate;


        Calendar c = Calendar.getInstance();

        double nextvalue = 0.0;
        double curvalue = 0.0;

        YPositionMetric point = new YPositionMetric(100, YLayoutStyle.ABSOLUTE_FROM_TOP);
        XValueMarker marker;
   
        for (int i = 0; i < count; i++) {
            objH = scoreList.get(i);
            yValues[i + 1] = objH.getInt("bloodsugar");
            int type = objH.getInt("mealtime");
            String current = "Date:" + objH.getDate("testtime").toString() + "\n";

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

            if (type == 2) {
                curvalue = nextvalue + 0.9;
                current += "בארוחת ערב\n";
  
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

        mySimpleXYPlot.setRangeBoundaries(0, 710, BoundaryMode.FIXED);


        mySimpleXYPlot.setDomainLabel("Blood sugar");
        mySimpleXYPlot.setRangeLabel("Blood sugar");
        mySimpleXYPlot.setTitle("Blood sugar");
        mySimpleXYPlot.setOnTouchListener(this);
        mySimpleXYPlot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat());

        XYSeries series = new SimpleXYSeries(Arrays.asList(xValues), Arrays.asList(yValues), "תאריך");
        mySimpleXYPlot.addSeries(series, new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100), null, new PointLabelFormatter(Color.WHITE)));
        mySimpleXYPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        mySimpleXYPlot.setDomainStepValue(1);

        mySimpleXYPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        mySimpleXYPlot.setRangeStepValue(50);

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



    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
              

                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                stopThread = true;
                break;



            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;

            case MotionEvent.ACTION_POINTER_DOWN: 
                distBetweenFingers = spacing(event);

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

        minXY.x = Math.min(minXY.x, 31);

        maxXY.x = Math.max(maxXY.x, 0);

        clampToDomainBounds(domainSpan);
    }

    private void scroll(float pan) {

        float domainSpan = maxXY.x - minXY.x;
        float step = domainSpan / mySimpleXYPlot.getWidth();

        float offset = pan * step;
        minXY.x = minXY.x + offset;
        maxXY.x = maxXY.x + offset;
        clampToDomainBounds(domainSpan);
    }

    private void clampToDomainBounds(float domainSpan) {
        float leftBoundary = 0;
        float rightBoundary = 31;

        if (minXY.x < leftBoundary) {
            minXY.x = leftBoundary;
            maxXY.x = leftBoundary + domainSpan;
        } else if (maxXY.x > 31) {
            maxXY.x = rightBoundary;
            minXY.x = rightBoundary - domainSpan;
        }
    }


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

    class SendToEmail extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            runOnUiThread(new Runnable() {
                public void run() {


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

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(graph.this);
            builder.setTitle("שליחת נתונים לאימייל").setMessage("שליחת נתוני הבדיקות לאימייל התבצעה בהצלחה!")
                    .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

}