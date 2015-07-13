## Server Setup

### Understanding Parse

The server is deployed on [Parse](https://www.parse.com/). 

#### Steps to create an app in Parse Server:
* Open [Parse](https://www.parse.com/). And sign in with your email address if you have one or create a new one.
* After successful sign in create an app with Some name.
* Then, perform the following setups mentioned in this readme for further



### Server Data Config
After Installing the setup configure the data bases in Parse:
* Data Table - _Installation -> Add an extra Column "DeviceID" - type -> string<br>
* Data Table - Device -> Add columns:<br>
  * DeviceID - type(String)
  * GeoLocation - type(GeoPoint)
  * WaterStatus - type(BOOLEAN)
  * LightStatus - type(BOOLEAN)
  * IlluminatedTime- type(Date)
  * WateredTime - type(Date)
  * WeatherStatus - type(String)
  * WeatherDesc - type(String)
  * Humidity - type(number)
  * Temperature - type(number)
  * Pressure - type(number)

* Data Table - PollQueue -> Add columns:<br>
  * DeviceID - type(String)
  * WaterStatus - type(BOOLEAN)
  * LightStatus - type(BOOLEAN)
  * OverrideTime- type(Date)

### Cloud Code Setup
* Use this [setup](https://www.parse.com/apps/quickstart#cloud_code/unix) for installing parse cloud code in your system.
* After Cloud code setup is done. Copy the main.js from Github repo to your machine location where the cloud code was setup.
