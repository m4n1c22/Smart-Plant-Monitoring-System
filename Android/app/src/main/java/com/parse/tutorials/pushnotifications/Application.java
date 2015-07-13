package com.parse.tutorials.pushnotifications;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.Parse;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

public class Application extends android.app.Application {

  String registeredDeviceID = "";
  Boolean isSubscribtedToADevice = false;

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// Initialize the Parse SDK.
	Parse.initialize(this, "A1mhNBAj92MCA2vvLgWs9d1iGudjvTLt72PNWuo8", "C9tXTJ5YxLeRhD4r9IxONRDtI52hLuRVXx3ASX0a");

	// Specify an Activity to handle all pushes by default.
	PushService.setDefaultPushCallback(this, MainActivity.class);


  }
}