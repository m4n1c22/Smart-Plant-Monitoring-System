package com.parse.tutorials.pushnotifications;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


public class MainActivity extends FragmentActivity {

	Application app;
	String deviceToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		app = ((Application)getApplication());

	}

	@Override
	public void onStart() {
		super.onStart();

		final ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();

		currentInstallation.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {

					// try to geht the device token
					// exception when device has no play services!
					try {
						deviceToken = currentInstallation.get("deviceToken").toString();
					} catch (RuntimeException e1) {
						Toast.makeText(getApplicationContext(), "PlaySerices are missing! Using random String as device token.", Toast.LENGTH_SHORT).show();
						deviceToken = "APB91bEGjHu7jB-zzCoUhgxgOsnM2-gYRKbcUDjeIgMN2xcxds-eZ7AVocUopPr3CvPFUtLJUE2Wf3rMXX2kX7_My8o1b_NQ0tkETZ2D8XHzg5ygZMHWytcxm3shNU6USCJnx63j20_BSMveidH1vwiyZWIbpXm6zw";
					}

					if (app.isSubscribtedToADevice) {

						// request current weather data
						loadCurrentWeather();
						//request tomorrows weather data
						loadTomorrowWeather();
						//request device status
						loadDeviceStatus();

					} else {

						openNewDeviceIdDialog(null);


					}

					Toast.makeText(getApplicationContext(), "Fetching data...", Toast.LENGTH_SHORT).show();


					//Log.d("devicetoken", deviceToken);
				} else {
					Log.d("tk3", "was not able to call extDeviceRegister");
				}
			}
		});


	}



	private void registerDevice(){
		Log.d("tk3", deviceToken.toString());

		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("DeviceID", app.registeredDeviceID);

		ParseCloud.callFunctionInBackground("extDeviceRegister", map, new FunctionCallback<Object>() {

			public void done(Object result, ParseException e) {
				Log.d("tk3", "called extdeviceregister");


				app.isSubscribtedToADevice = true;

				if (e == null) {
					// result is "Hello world!"
					Log.d("tk3", result.toString());
				} else {
					Log.d("tk3", e.toString());
				}


				// request current weather data
				loadCurrentWeather();
				//request tomorrows weather data
				loadTomorrowWeather();
				//request device status
				loadDeviceStatus();

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

				//Log.d("tk3", result.toString());

				if (e == null) {

					JSONObject json = new JSONObject(result);

					try{
						// set device last watered info
						TextView textView_LastWatered;
						textView_LastWatered = (TextView) findViewById(R.id.lastWateredValue);
						textView_LastWatered.setText(json.get("WateredTime").toString());
					} catch (JSONException e1) {
						e1.printStackTrace();
					}

					try{
						// set current device light status
						TextView textView_StatusLight;
						textView_StatusLight = (TextView) findViewById(R.id.statusLight);
						if (json.get("LightStatus").toString() == "true") {
							textView_StatusLight.setText("Light is on!");
							//light is on, enable override button
							Button button_light_off = (Button) findViewById(R.id.button_light_off);
							button_light_off.setEnabled(true);
						} else {
							textView_StatusLight.setText("Light is off!");
							//light is on, enable override button
							Button button_light_off = (Button) findViewById(R.id.button_light_off);
							button_light_off.setEnabled(false);
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
		map.put("ExtDeviceID", deviceToken);

		ParseCloud.callFunctionInBackground("weatherForecastForDevice", map, new FunctionCallback<HashMap>() {

			public void done(HashMap result, ParseException e) {
				Log.d("tk3", "called weatherPredictionForDevice");

				if (e == null) {

					try {
						JSONObject json = new JSONObject(result);

						// set tomorrow temperature
						TextView textView_TomorrowTemp;
						textView_TomorrowTemp = (TextView) findViewById(R.id.tomorrowTempValue);
						double tomorrowTempValue = Double.parseDouble(json.get("Temperature").toString()) - 273;
						textView_TomorrowTemp.setText(new Integer((int) tomorrowTempValue).toString() + (char) 0x00B0 + "C");

						// set tomorrow temperature
						TextView textView_TomorrowHumidity;
						textView_TomorrowHumidity = (TextView) findViewById(R.id.tomorrowHumidityValue);
						double tomorrowHumidityValue = Double.parseDouble(json.get("Humidity").toString());
						textView_TomorrowHumidity.setText(new Integer((int) tomorrowHumidityValue).toString() + "%");


						// set tomorrow temperature
						TextView textView_TomorrowPressure;
						textView_TomorrowPressure = (TextView) findViewById(R.id.tomorrowPressureValue);
						double tomorrowPressureValue = Double.parseDouble(json.get("Pressure").toString()) - 273;
						textView_TomorrowPressure.setText(new Integer((int) tomorrowPressureValue).toString() + " HPA");


						// set tomorrow temperature
						TextView textView_TomorrowStatus;
						textView_TomorrowStatus = (TextView) findViewById(R.id.tomorrowStatusValue);
						textView_TomorrowStatus.setText(json.get("Weather").toString());


					} catch (JSONException e1) {
						e1.printStackTrace();
					}

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
	public void overrideStartWater(View view){
		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("WaterStatus", "true");

		//request override
		ParseCloud.callFunctionInBackground("manualOverride", map, new FunctionCallback<String>() {

			public void done(String result, ParseException e) {
				Log.d("tk3", "called manualOverride WaterStatus ");
				Log.d("tk3", result);

				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

				if (e == null) {
					Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}


	/**
	 * Function called to override water status on the remote device (enable/disable watering)
	 * @param view
	 */
	public void overrideBlockWater(View view){
		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("WaterStatus", "false");

		//request override
		ParseCloud.callFunctionInBackground("manualOverride", map, new FunctionCallback<String>() {

			public void done(String result, ParseException e) {
				Log.d("tk3", "called manualOverride WaterStatus ");
				Log.d("tk3", result);

				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

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
	public void overrideStartLight(View view){
		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("LightStatus", "true");

		//request override
		ParseCloud.callFunctionInBackground("manualOverride", map, new FunctionCallback<String>() {

			public void done(String result, ParseException e) {
				Log.d("tk3", "called manualOverride LightStatus");
				Log.d("tk3", result);

				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

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
	public void overrideStopLight(View view){
		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("LightStatus", "false");

		//request override
		ParseCloud.callFunctionInBackground("manualOverride", map, new FunctionCallback<String>() {

			public void done(String result, ParseException e) {
				Log.d("tk3", "called manualOverride LightStatus");
				Log.d("tk3", result);

				Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

				if (e == null) {
					Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	public void openNewDeviceIdDialog(View view){
		FragmentManager fm = getSupportFragmentManager();

		InsertDeviceID alertdFragment = new InsertDeviceID();
		// Show Alert DialogFragment
		alertdFragment.show(fm, "Alert Dialog Fragment");

	}

	public void setNewDeviceId(String newId){
		app.registeredDeviceID = newId;
		Log.d("tk3", "new deviceId = "+newId);
		registerDevice();
	}

	public void reloadDeviceData(View view){
		Toast.makeText(getApplicationContext(), "Refresh...", Toast.LENGTH_SHORT).show();
		loadDeviceStatus();
	}


	@Override
	protected void onNewIntent(Intent intent) {
		loadDeviceStatus();
	}
}