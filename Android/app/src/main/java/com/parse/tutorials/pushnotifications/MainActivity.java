package com.parse.tutorials.pushnotifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

String deviceToken;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		examineIntent(getIntent());




/*
		*/
	}

	@Override
	public void onStart() {
		super.onStart();
		
		final ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();

		currentInstallation.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {
					//deviceToken = currentInstallation.get("deviceToken").toString();
					deviceToken = "APA91bEGjHu7jB-zzCoUhgxgOsnM3-gYRKbcUDjeIgMNLxcxds-eZ7AVocUopPr3CvPFUtLJUE2Wf3rMXX2kX7_My8o1b_NQ0tkETZ2D8XHzg5ygZMHWytcxm3shNU6USCJnx63j20_BSMveidH1vwiyZWIbpXm6zw";

					Map map = new HashMap();
					map.put("ExtDeviceID", deviceToken);
					map.put("DeviceID", "100001");

					ParseCloud.callFunctionInBackground("extDeviceRegister", map, new FunctionCallback<Object>() {

						public void done(Object result, ParseException e) {
							Log.d("tk3", "called extdeviceregister");

							if (e == null) {
								// result is "Hello world!"
								Log.d("tk3", result.toString());
							} else {
								Log.d("tk3", e.toString());
							}
						}
					});


					Toast.makeText(getApplicationContext(), "Fetching data...", Toast.LENGTH_SHORT).show();

					// request current weather data
					loadCurrentWeather();
					//request tomorrows weather data
					loadTomorrowWeather();
					//request device status
					loadDeviceStatus();

					//Log.d("devicetoken", deviceToken);
				} else {
					Log.d("tk3", "was not able to call extDeviceRegister");
				}
			}
		});


	}

	/**
	 *  Function used to load current weather data from parse and output it to the gui
	 */
	private void loadCurrentWeather(){

		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);

		//send parse request
		ParseCloud.callFunctionInBackground("weatherStatusForDevice", map, new FunctionCallback<HashMap>() {

			public void done(HashMap result, ParseException e) {
				Log.d("tk3", "called weatherPredictionForDevice");

				//exception handling
				if (e == null) {

					try {
						//convert result to JSONObject for access
						JSONObject json = new JSONObject(result);

						// set todays temperature
						TextView textView_TodayTemp;
						textView_TodayTemp = (TextView) findViewById(R.id.todayTempValue);
						double todayTempValue = Double.parseDouble(json.get("Temperature").toString()) - 273;
						textView_TodayTemp.setText(new Integer((int) todayTempValue).toString() + (char) 0x00B0 + "C");

						// set todays humidity
						TextView textView_TodayHumidity;
						textView_TodayHumidity = (TextView) findViewById(R.id.todayHumidityValue);
						double todayHumidityValue = Double.parseDouble(json.get("Humidity").toString());
						textView_TodayHumidity.setText(new Integer((int) todayHumidityValue).toString() + "%");


						// set todays pressure
						TextView textView_TodayPressure;
						textView_TodayPressure = (TextView) findViewById(R.id.todayPressureValue);
						double todayPressureValue = Double.parseDouble(json.get("Pressure").toString()) - 273;
						textView_TodayPressure.setText(new Integer((int) todayPressureValue).toString() + " HPA");


						// set todays status
						TextView textView_TodayStatus;
						textView_TodayStatus = (TextView) findViewById(R.id.todayStatusValue);
						textView_TodayStatus.setText(json.get("WeatherDesc").toString());


					} catch (JSONException e1) {
						e1.printStackTrace();
					}

				} else {
					//output when request caused exception
					Toast.makeText(getApplicationContext(), "could not load todays weather!", Toast.LENGTH_LONG).show();
					Log.d("tk3", e.toString());
				}
			}
		});

	}

	/**
	 * Function used to load the current device status from parse (last watered, light status, water status)
	 */
	private void loadDeviceStatus(){

		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);

		ParseCloud.callFunctionInBackground("deviceStatus", map, new FunctionCallback<HashMap>() {

			public void done(HashMap result, ParseException e) {
				Log.d("tk3", "called deviceStatus");

				if (e == null) {

					try {
						// convert result to JSON for access
						JSONObject json = new JSONObject(result);


						// set device last watered info
						TextView textView_LastWatered;
						textView_LastWatered = (TextView) findViewById(R.id.lastWateredValue);
						textView_LastWatered.setText(json.get("WateredTime").toString());

						// set current device light status
						TextView textView_StatusLight;
						textView_StatusLight = (TextView) findViewById(R.id.statusLight);
						if (json.get("LightStatus").toString() == "true") {
							textView_StatusLight.setText("Light is on!");
						} else {
							textView_StatusLight.setText("Light is off!");
						}

						// set current device water status
						TextView textView_StatusWater;
						textView_StatusWater = (TextView) findViewById(R.id.statusWater);
						if (json.get("WaterStatus").toString() == "true") {
							textView_StatusWater.setText("Water is on!");
						} else {
							textView_StatusWater.setText("Water is off!");
						}


					} catch (JSONException e1) {
						e1.printStackTrace();
					}

				} else {
					//output when request caused exception
					Toast.makeText(getApplicationContext(), "could not load device status!", Toast.LENGTH_LONG).show();
					//Log.d("tk3", e.toString());
				}
			}
		});

	}

	/**
	 *  Function used to load tomorrows weather data from parse and output it to the gui
	 */
	private void loadTomorrowWeather(){

		Map map = new HashMap();
		map.put("DeviceID", "100001");

		ParseCloud.callFunctionInBackground("weatherForecastForDevice", map, new FunctionCallback<HashMap>() {

			public void done(HashMap result, ParseException e) {
				Log.d("tk3", "called weatherPredictionForDevice");

				if (e == null) {
/*
					try {
						JSONObject json = new JSONObject(result);

						// set todays temperature
						TextView textView_TodayTemp;
						textView_TodayTemp = (TextView) findViewById(R.id.todayTempValue);
						double todayTempValue = Double.parseDouble(json.get("Temperature").toString()) - 273;
						textView_TodayTemp.setText(new Integer((int) todayTempValue).toString() + (char) 0x00B0 + "C" );

						// set todays temperature
						TextView textView_TodayHumidity;
						textView_TodayHumidity = (TextView) findViewById(R.id.todayHumidityValue);
						double todayHumidityValue = Double.parseDouble(json.get("Humidity").toString());
						textView_TodayHumidity.setText(new Integer((int) todayHumidityValue).toString() + "%" );


						// set todays temperature
						TextView textView_TodayPressure;
						textView_TodayPressure = (TextView) findViewById(R.id.todayPressureValue);
						double todayPressureValue = Double.parseDouble(json.get("Pressure").toString()) - 273;
						textView_TodayPressure.setText(new Integer((int) todayPressureValue).toString() + " HPA" );


						// set todays temperature
						TextView textView_TodayStatus;
						textView_TodayStatus = (TextView) findViewById(R.id.todayStatusValue);
						textView_TodayStatus.setText(json.get("WeatherDesc").toString());


					} catch (JSONException e1) {
						e1.printStackTrace();
					}*/

					Log.d("tk3", result.toString());
				} else {
					//output when request caused exception
					Toast.makeText(getApplicationContext(), "could not load tomorrows weather!", Toast.LENGTH_LONG).show();
					Log.d("tk3", e.toString());
				}
			}
		});
	}

	/**
	 * Function called to override water status on the remote device (enable/disable watering)
	 * @param view
	 */
	public void overrideWater(View view){
		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("WaterStatus", "true");

		//request override
		ParseCloud.callFunctionInBackground("manualOverride", map, new FunctionCallback<HashMap>() {

			public void done(HashMap result, ParseException e) {
				Log.d("tk3", "called manualOverride WaterStatus ");

				if (e == null) {
					Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * Function called to override light status on the remote device (enable/disable watering)
	 * @param view
	 */
	public void overrideLight(View view){
		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("LightStatus", "true");

		//request override
		ParseCloud.callFunctionInBackground("manualOverride", map, new FunctionCallback<HashMap>() {

			public void done(HashMap result, ParseException e) {
				Log.d("tk3", "called manualOverride LightStatus");

				if (e == null) {
					Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}



	@Override
	protected void onNewIntent(Intent intent) {
		//examineIntent(intent);
	}

	private void examineIntent(Intent i)
	{
		//String u = i.toURI();
		//TextView tv = (TextView)findViewById(R.id.today);
		//tv.setText(u);
	}
}