;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Random;



public class graph extends Activity implements OnTouchListener
{

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //Sharedpreferences is an effective and convenient mechanism for saving data.
        // The data are saved in pairs key / value.
        final SharedPreferences spUser = getSharedPreferences("USER", Activity.MODE_PRIVATE);

        //Connection between XML component to JAVA class
        email=spUser.getString("user","");
        txtMyDate = (EditText) findViewById(R.id.etxtMyDate);
        SetDate setDate = new SetDate(txtMyDate, this);
        imgback = (ImageView) findViewById(R.id.imgBack);
        imgback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
                finish();
            }
        });
        txtMyDate.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
        //get from PARSE the table history
        ParseQuery<ParseObject> query = ParseQuery.getQuery("History");
        // Sorts the results in descending order by the score field
        query.addAscendingOrder("mysort");
        query.whereEqualTo("email", LoginActivity.emailid);
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> scoreList, ParseException e)
            {
                if (e == null)
                {
                    if (scoreList.size() != 0)
                    {
                        uploadData(scoreList);
                    }

                }
                else
                {

                }
            }
        });
        btnOption = (Button) findViewById(R.id.btnOption);
        btnOption.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(graph.this);
                builder.setTitle("אפשרויות");
                builder.setItems(itemList, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (i == 0)//the user choosed send to email
                        {
                            if (!iscapture)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(graph.this);
                                // Specify the list in the dialog using the array
                                builder.setTitle("שליחת אימייל").setMessage("לפני שליחת בדיקות לאימייל יש לחתוך את התמונה! ")
                                        .setPositiveButton("אישור", new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialog, int id)
                                            {

                                            }
                                        });

                                //create and show list dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else
                            {
                                pDialog = new ProgressDialog(graph.this);
                                pDialog.setMessage("שולח בדיקות לאימייל אנא המתן...");
                                //"loading amount" is not measured.
                                pDialog.setIndeterminate(false);
                                //Sets whether this dialog is cancelable with the BACK key
                                pDialog.setCancelable(true);
                                pDialog.show();
                                Thread th = new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {

                                        while (imgurl.equals("empty"))
                                        {
                                            try
                                            {
                                                Thread.sleep(5000);

                                            }
                                            catch (Exception ae)
                                            {
                                            }
                                        }
                                        comment += "\n" + " קישור לתמונת הגרף:" + imgurl;
                                        new SendToEmail().execute( comment);
                                    }
                                });
                                //to new Runnable() in asyncrone Task
                                th.start();

                            }
                        }
                        else
                        {
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

        btnCapture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //go to picture table when the email equall to email user
                ParseObject gameScore = new ParseObject("Pictures");
                gameScore.put("email", LoginActivity.emailid);
                //Enables or disables the Cache cache.
                //When the drawing cache is enabled, the next call to getDrawingCache() or buildDrawingCache()
                // will draw the view in a bitmap.

                //we open the cache to the pic
                mySimpleXYPlot.setDrawingCacheEnabled(true);

                FileOutputStream fos = null;
                try
                {
                    //The path of images saved to the user's device
                    //if the second argument is true, then bytes will be written to the end of the file rather than the beginning
                    fos = new FileOutputStream("/sdcard/DCIM/img.jpg", false);
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

                    gameScore.saveInBackground(new SaveCallback()
                    {
                        @Override
                        public void done(ParseException e)
                        {
                            imgurl = pfile.getUrl().toString();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        });


    }

  //method that uplad all the Data
    public void uploadData(List<ParseObject> scoreList)
    {
        //Get a reference to the XYplot defined in the XML.
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        if (scoreList.size() <= 0) return;
        ParseObject objH = scoreList.get(0);
        Random rn = new Random();
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
        yValues[0] = objH.getInt("bloodsugar");
        xValues[0] = 0;
        xLabels[0] = formattedDate;

        // Create a Calendar object
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //incrememnt 1 day to data

       //c.add(Calendar.DATE, 1);
        //for example: May 13 16:51:49
        date = new Date(c.getTimeInMillis());
        double nextvalue = 0.0;
        double curvalue = 0.0;
        //postioin to all meals titels
        YPositionMetric point = new YPositionMetric(100, YLayoutStyle.ABSOLUTE_FROM_TOP);
        XValueMarker marker;
        for (int i = 0; i < count; i++)
        {
            objH = scoreList.get(i);
            yValues[i] = objH.getInt("bloodsugar");
            int type = objH.getInt("mealtime");
            String current = "Date:" + objH.getDate("testtime").toString() + "\n";
            //if we are in the breakfast:
            if (type == 0)
            {
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
            if (type == 1)
            {
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
            if (type == 2)
            {
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
            current += "רמת הסוכר שלך היא:" + objH.getInt("bloodsugar") + "\n";
            current += "אכלת:" + objH.getString("information") + "\n";
            xValues[i + 1] = curvalue;
            c.setTime(date);
            c.add(Calendar.DATE, 1);
            date = new Date(c.getTimeInMillis());
            formattedDate = format.format(date);
            xLabels[i + 1] = formattedDate;

            comment += current + "\n----------------------------------------\n";
        }
        //after we finish 1 day we past to next dey
        c.setTime(date);
        date = new Date(c.getTimeInMillis());
        formattedDate = format.format(date);
        yValues[count + 1] = yValues[count];//new Number[]{80,80,90,70,100,120,120};
        xValues[count + 1] = nextvalue;//Number[]{0,0.3,0.6,0.9,1.3,1.6,2};
        xLabels[count + 1] = formattedDate;//new String[]{"17/11","18/11","19/11","20/11","21/11","22/11"};



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
        mySimpleXYPlot.addSeries(series,new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100), null, new PointLabelFormatter(Color.WHITE)));
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