
package com.parse.DiabetsApplication;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

//This class is necessary to install cloud PARSE.  Without Her project will not working
public class DiabetsApplication extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        //These exclusive keys of my application made by PARSE
        Parse.initialize(this, "B96XqoQToGe0J1rc5u29qAkZ4zmeFno244nJFdLQ", "zUjEFXgN4IczmS3NQaWseSEUGUJDX8fyRkZ8oGzi");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
