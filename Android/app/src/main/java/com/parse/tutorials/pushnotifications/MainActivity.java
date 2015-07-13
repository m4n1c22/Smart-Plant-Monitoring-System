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

		// get the current parse installation
		final ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();

		// save the current parse installation, so we can use it later
		currentInstallation.saveInBackground(new SaveCallback() {
			public void done(ParseException e) {
				if (e == null) {

					// try to get the device token
					// exception when device has no play services!
					try {
						// get device token
						deviceToken = currentInstallation.get("deviceToken").toString();
					} catch (RuntimeException e1) {
						// catch exception if play services are not installed
						// use fixed string. No push Servies are possible
						Toast.makeText(getApplicationContext(), "PlaySerices are missing! Using random String as device token.", Toast.LENGTH_SHORT).show();
						deviceToken = "idH1vwiyZWIbpXm6zw-No-play-services";
					}

					// if the app is already subscribed to a device
					// do not subscribe again, just load data
					// this happens when the app is running in the background and was reopend
					if (app.isSubscribtedToADevice) {

						// request current weather data
						loadCurrentWeather();
						//request tomorrows weather data
						loadTomorrowWeather();
						//request device status
						loadDeviceStatus();

						Toast.makeText(getApplicationContext(), "Fetching data...", Toast.LENGTH_SHORT).show();

					// if the device was not running in the background, the deviceId has to be set
					} else {
						// open dialog to ask the user for the deviceId.
						openNewDeviceIdDialog(null);
					}

				} else {
					Log.d("tk3", "was not able to call extDeviceRegister");
				}
			}
		});


	}


	/**
	 * Function used to register the device in parse
	 */
	private void registerDevice(){
		Log.d("tk3", deviceToken.toString());

		// build hash map with the reqiered data which will be send to parse
		Map map = new HashMap();
		map.put("ExtDeviceID", deviceToken);
		map.put("DeviceID", app.registeredDeviceID);

		// call function in parse
		ParseCloud.callFunctionInBackground("extDeviceRegister", map, new FunctionCallback<Object>() {

			// callback if registration was done
			public void done(Object result, ParseException e) {
				Log.d("tk3", "called extdeviceregister");

				// check if there was an exception or not
				if (e == null) {
					// set the flag isSubsribedToADevice to true, so the app will not register again next time
					app.isSubscribtedToADevice = true;

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

	/**
	 * Function to open a dialog and ask the user for a deviceId
	 * @param view
	 */
	public void openNewDeviceIdDialog(View view){
		FragmentManager fm = getSupportFragmentManager();
		InsertDeviceID alertdFragment = new InsertDeviceID();
		// Show Alert DialogFragment
		alertdFragment.show(fm, "Alert Dialog Fragment");
	}

	/**
	 * Function called by dialog, to set a new deviceId
	 * Triggers device registration in parse
	 * @param newId
	 */
	public void setNewDeviceId(String newId){
		app.registeredDeviceID = newId;
		Log.d("tk3", "new deviceId = "+newId);
		registerDevice();
	}

	/**
	 * Function calling an update request on the device status. Used by refresh button
	 * @param view
	 */
	public void reloadDeviceData(View view){
		Toast.makeText(getApplicationContext(), "Refresh...", Toast.LENGTH_SHORT).show();
		loadDeviceStatus();
	}

	/**
	 * Function called by incoming Notification. Triggers update request
	 * @param intent
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		loadDeviceStatus();
	}
}